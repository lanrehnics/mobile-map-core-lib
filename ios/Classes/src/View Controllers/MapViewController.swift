//
//  MapViewController.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 22/10/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import UIKit
import GoogleMaps
import FirebaseFirestore

class MapViewController: UIViewController, GMSMapViewDelegate,UISearchBarDelegate,
 CLLocationManagerDelegate, GMUClusterRendererDelegate, GMUClusterManagerDelegate{

    var mapView: GMSMapView!
    var truckGreenIcon: UIImageView?
    var truckRedIcon: UIImageView?
    var truckBlueIcon: UIImageView?
    var custLocationsIcon: UIImageView?
    var defaultMarker: GMSMarker?
    var truckDetailsViewController: TruckDetailsViewController!
    var visualEffectView: UIVisualEffectView!
    var endCardHeight: CGFloat = 0
    var startCardHeight: CGFloat = 0
    var cardVisible = false
    var runningAnimations = [UIViewPropertyAnimator]()
    var animationProgressWhenInterrupted: CGFloat = 0
    var filterBtn: UIImageView!
    // set when a truck on the map is clicked
    var trackingTruck = false
    private var truckAnimator: TruckAnimator!
    private var networkUtil = NetworkUtil()
    var filterButton: UIButton!
    var closeBtn: UIButton!
    var filterModel = FilterModel()
    var isFirstTImeLoad = true
    var bounds = GMSCoordinateBounds()
    var isInFilteringProcess = false
    var searchbar: UISearchBar!
    var searchButton: RoundButton!
    var currentUserLocation: CLLocationCoordinate2D!
    var firebaseColRef: CollectionReference!
    var clusterManager: GMUClusterManager!
    var mapDataState = MapDataState.initial
    var currentQuery: GFSCircleQuery!
    var singleDocListener: ListenerRegistration?
    
    private var markers: [String : POIItem] = [:]
    private var trucks: [String : Truck] = [:]

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        self.pulleyViewController?.displayMode = .drawer
        self.pulleyViewController?.setDrawerPosition(position: .closed, animated: false)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        self.view = mapView
        getCurrentLocation()
        setUpMarkerCluster()
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    func setUpMarkerCluster() {
        let iconGenerator = GMUDefaultClusterIconGenerator()
        //let iconGenerator = GMUDefaultClusterIconGenerator(buckets: [5, 20, 50], backgroundImages: [icon!, blue!, red!])
        let algorithm = GMUNonHierarchicalDistanceBasedAlgorithm()
        let renderer = GMUDefaultClusterRenderer(mapView: mapView, clusterIconGenerator: iconGenerator)
        renderer.delegate = self
        clusterManager = GMUClusterManager(map: mapView, algorithm: algorithm, renderer: renderer)
        clusterManager.setDelegate(self, mapDelegate: self)
    }
    
    func getCurrentLocation() {
        if CLLocationManager.locationServicesEnabled() {
            let locationManager = CLLocationManager()
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.requestAlwaysAuthorization()
            if let location = locationManager.location {
                currentUserLocation = location.coordinate
                loadTrucks()
            }
            locationManager.requestLocation()
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        if #available(iOS 13.0, *) {
            return .darkContent
        } else {
            return .default
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("Location is here")
        if currentUserLocation != nil {return}
        if let location = locations.last {
            print("User current location", location.coordinate)
//            currentUserLocation = location.coordinate
//            mapView.animate(toLocation: currentUserLocation)
//            loadTrucks()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("An error has occured here when getting location ", error.localizedDescription)
    }
    
    override func loadView() {
        currentUserLocation = CLLocationCoordinate2D(latitude: 6.5212402, longitude: 3.3679965)
        let camera = GMSCameraPosition(latitude: 6.5212402, longitude: 3.3679965, zoom: 15)
        mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        mapView.isTrafficEnabled = false
        mapView.isMyLocationEnabled = true
        mapView.settings.compassButton = false
        mapView.settings.myLocationButton = true
        mapView.settings.zoomGestures = true
        mapView.settings.tiltGestures = false
        mapView.settings.rotateGestures = false
        
        mapView.delegate = self
        let icon = UIImage(named: "truck_green")
        truckGreenIcon = UIImageView(image: icon)
        let blue = UIImage(named: "truck_blue")
        let red = UIImage(named: "truck_red")
        let custLoc = UIImage(named: "customer_locations")
        custLocationsIcon = UIImageView(image: custLoc)
        truckBlueIcon = UIImageView(image: blue)
        truckRedIcon = UIImageView(image: red)
        mapView.setMinZoom(8, maxZoom: 18)
        

        do {
           if let styleUrl = Bundle.main.url(forResource: "style", withExtension: "json") {
               mapView.mapStyle = try GMSMapStyle(contentsOfFileURL: styleUrl)
           } else {
               NSLog("Unable to find style file")
           }
        } catch {
           NSLog("one or more of the file styles failed to ooad")
        }
        loadTrucks()
    }
    
    func loadTrucks() {
        print("connecting to firebase")
        firebaseColRef = Firestore.firestore().collection("Trucks")
        let geoFireStore = GeoFirestore(collectionRef: firebaseColRef)
        let center = CLLocation(latitude: currentUserLocation.latitude, longitude: currentUserLocation.longitude)
        currentQuery = geoFireStore.query(withCenter: center, radius: 10) // 60Km
        
        _ = currentQuery.observe(.documentEntered, with: {
            (snapshot: DocumentSnapshot?, location) in
            self.handleDocSnapshot(snapshot, state: .entered)
        })
        
        _ = currentQuery.observe(.documentMoved, with: { (snapshot: DocumentSnapshot?, location) in
            self.handleDocSnapshot(snapshot, state: . moved)
        })
        
        let _ = currentQuery.observeReady {
            self.clusterManager.cluster()
            self.mapView.animate(with: GMSCameraUpdate.fit(self.bounds, withPadding: 20))
        }
    }
    
    func handleDocSnapshot(_ snapshot: DocumentSnapshot?, state: DocumentState) {
        if let data = snapshot?.data() {
            if let truck = TruckModel(data: data)?.truck {
                switch mapDataState {
                    case .initial:
                        modifyTruckOnMap(truck, state)
                        break;
                case .filtering:
                    filterTruck(truck, state: state)
                default:
                    print("nothning")
                }
            }
        }
    }
    
    func addTruckToMap(truck: Truck) {
        if markers[truck.regNumber] == nil || mapDataState == .filtering {
           let position = CLLocationCoordinate2D(latitude: truck.lat, longitude: truck.long)
           bounds = bounds.includingCoordinate(position)
           let item = POIItem(position: position, data: truck)
           clusterManager.add(item)
           markers[truck.regNumber] = item
        }
    }
    
    func animateTruckOnMap(truck: Truck) {
        if let item = markers[truck.regNumber] {
            if let marker = item.marker {
                DispatchQueue.global(qos: .background).async {
                    DispatchQueue.main.async {
                        let fromLocation = CLLocationCoordinate2D(latitude: truck.lat, longitude: truck.long)
                        let toLocation = CLLocationCoordinate2D(
                           latitude: truck.nextPosition?.lat ?? truck.lat, longitude: truck.nextPosition?.long ?? truck.long)
                        self.truckAnimator = TruckAnimator(truckMarker: marker, mapView: self.mapView)
                        self.truckAnimator.animate(from: fromLocation, to: toLocation, bearing: Double(truck.bearing)!)
                    }
                }
            }
        }
    }
    
    func modifyTruckOnMap(_ truck: Truck, _ docState: DocumentState) {
        if(truck.lat == 0 || truck.long == 0) {return}
        switch docState {
            case .entered:
                addTruckToMap(truck: truck)
            case .moved:
                animateTruckOnMap(truck: truck)
            default:
                print("hsh")
        }
    }
    
    func addDestinationMarker(truck: Truck) {
        if truck.deliveryStation != nil && truck.deliveryStation!.lat > 0{
            let destMarker = GMSMarker()
            destMarker.position = CLLocationCoordinate2D(latitude: truck.deliveryStation!.lat, longitude: truck.deliveryStation!.long)
            destMarker.title = "\(truck.recipient)"
            destMarker.snippet = "\(truck.deliveryStation!.address)"
            destMarker.iconView = self.custLocationsIcon
            destMarker.map = self.mapView
        }
    }
    
    func mapViewDidStartTileRendering(_ mapView: GMSMapView) {
        buildFilterBtn()
        buildCloseBtn()
        buildSearchButton()
    }
    
    func renderer(_ renderer: GMUClusterRenderer, markerFor object: Any) -> GMSMarker? {
        var marker = GMSMarker()
        if let poiItem = object as? POIItem {
            marker = buildMarkerObj(poiItem)
        }
        return marker
    }
    
    func buildMarkerObj(_ poiItem: POIItem) -> GMSMarker {
        let marker = GMSMarker()
        marker.position = poiItem.position
        bounds = bounds.includingCoordinate(marker.position)
        marker.title = "\(poiItem.data.regNumber)"
        marker.rotation = Double(poiItem.data.bearing)!
        poiItem.data.isClustered = false
        if(poiItem.data.flagged) {
            marker.iconView = self.truckRedIcon
        } else if poiItem.data.active == 1 {
            marker.iconView = self.truckGreenIcon
        } else if(poiItem.data.speed > 0) {
            marker.iconView = self.truckGreenIcon
        } else {
            marker.iconView = self.truckBlueIcon
        }
        poiItem.marker = marker
        markers[poiItem.data.regNumber] = poiItem
        return marker
    }
    
    func buildFilterBtn() {
        if(filterButton == nil) {
            filterButton = RoundButton();
            filterButton.frame = CGRect(x: mapView.bounds.size.width - 65, y: mapView.bounds.size.height - 160,
                                  width: 56, height: 56);
            filterButton.tintColor = .blue
            filterButton.backgroundColor = .white
            filterButton.layer.cornerRadius = 28
            filterButton.clipsToBounds = true
            filterButton.setImage(UIImage(named: "filter"), for: .normal)
            filterButton.addTarget(self, action: #selector(openFilterView), for: .touchDown)
            filterButton.layer.shadowColor = UIColor.gray.cgColor
            filterButton.layer.shadowRadius = 12.0
            filterButton.layer.shadowOpacity = 0.7
            filterButton.layer.shadowOffset = CGSize(width: 0.0, height: 0.0)
            self.view.addSubview(filterButton)
        }
    }
    
    func buildCloseBtn() {
        if closeBtn == nil {
            closeBtn = RoundButton()
            closeBtn.frame = CGRect(x: 5, y: 20, width: 56, height: 56);
            closeBtn.addTarget(self, action: #selector(closeBtnAction), for: .touchDown)
            closeBtn.layer.cornerRadius = 28
            closeBtn.clipsToBounds = true
            closeBtn.setImage(UIImage(named: "arrow_back"), for: .normal)
            closeBtn.layer.shadowRadius = 12.0
            closeBtn.layer.shadowOpacity = 0.7
            self.view.addSubview(closeBtn)
        }
    }
    
    func buildSearchButton() {
        if searchButton == nil {
            searchButton = RoundButton()
            searchButton.frame = CGRect(x: mapView.bounds.size.width - 65, y: 20, width: 56, height: 56);
            searchButton.addTarget(self, action: #selector(onSearchButtonClicked), for: .touchDown)
            searchButton.backgroundColor = .white
           searchButton.layer.cornerRadius = 28
           searchButton.clipsToBounds = true
            //closeBtn.setImage(UIImage(named: "arrow_back"), for: .normal)
            searchButton.layer.shadowRadius = 12.0
            searchButton.layer.shadowOpacity = 0.7
            self.view.addSubview(searchButton)
        }
    }
    
    /*
     Click actions / functions
     */
    
    @objc func openFilterView(){
//        let filterVc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MapFilterViewController") as! MapFilterViewController
        let filterVc = FilterViewController()
        self.addChildViewController(filterVc)
        let height = view.frame.height
        let width  = view.frame.width
        filterVc.view.frame = CGRect(x: 0, y: self.view.frame.maxY, width: width, height: height)
        self.view.addSubview(filterVc.view)
        filterVc.setFilterModel(model: filterModel)
    }
    
    @objc func onSearchButtonClicked() {
//        let searchVc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SearchViewController") as! SearchViewController
        let searchVc = SearchViewController()
        searchVc.modalPresentationStyle = .formSheet
        searchVc.mapVC = self
        self.present(searchVc, animated: true, completion: nil)
    }
    
    @objc func closeBtnAction() {
        if mapDataState == .singleTruckFocus {
            closeBtn.setImage(UIImage(named: "arrow_back"), for: .normal)
            mapDataState = .initial
            singleDocListener?.remove()
            mapView.clear();
            mapView.animate(with: GMSCameraUpdate.setCamera(GMSCameraPosition(latitude: currentUserLocation.latitude, longitude: currentUserLocation.longitude, zoom: 12)))
            markers = [:]
            filterButton.isHidden = false
            searchButton.isHidden = false
            bounds = GMSCoordinateBounds()
            loadTrucks()
            self.pulleyViewController?.setDrawerPosition(position: .closed, animated: false)
            
        } else {
            dismiss(animated: true, completion: nil)
        }
    }
    
    func didTapMyLocationButton(for mapView: GMSMapView) -> Bool {
        getCurrentLocation()
        return true
    }
    
    /*
     On marker clicked event, this event is broadcasted when
     a marker is tapped
     */
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        if (marker.title == nil) {return false}
        
        if let truck = markers[marker.title!]!.data {
            onSingleTruckFocus(truck)
            return true
        } else {
            return false
        }
        
    }
    
    func filterTruck(_ truck: Truck, state: DocumentState = .entered) {
        if(filterModel.flagged && truck.flagged)
            || (filterModel.inPremise && truck.status.lowercased() == "in-premise")
            || (filterModel.positioned && truck.status.lowercased() == "positioned")
            || (filterModel.loaded && truck.status.lowercased() == "loaded")
            || (filterModel.atDestination && truck.status.lowercased() == "at-destination")
            || (filterModel.availableTruck && truck.delivered)
        {
            modifyTruckOnMap(truck, state)
        }
    }
    
    func applyFilters(model: FilterModel) {
        print("applying filters for ---- ", model)
        filterModel = model
        bounds = GMSCoordinateBounds()
        self.isFirstTImeLoad = true
        clusterManager.clearItems()
        mapDataState = .filtering
        for marker in markers.values {
            filterTruck(marker.data)
        }
        self.clusterManager.cluster()
        self.mapView.animate(with: GMSCameraUpdate.fit(self.bounds, withPadding: 20))

    }
    
    func clearFilters(model: FilterModel) {
        print("clearing filters")
        filterModel = model
        mapView.clear()
        bounds = GMSCoordinateBounds()
        mapDataState = .initial
        markers = [:]
        loadTrucks()
    }
    
    func search(searchModel: SearchResultModel) {
        mapDataState = .searching
        currentQuery.removeAllObservers()
        switch searchModel.type {
            case .address:
                let placemark = searchModel.data as! CLPlacemark
                if let location = placemark.location {
                    currentUserLocation = location.coordinate
                    clearFilters(model: filterModel)
                    mapView.animate(toLocation: currentUserLocation)
                }
                break
        case .truck:
            let truck = searchModel.data as! Truck
            onSingleTruckFocus(truck)
            break
        default:
            print("jsjsjd")
        }
    }
    
    func onSingleTruckFocus(_ truck: Truck) {
        currentQuery.removeAllObservers()
        bounds = GMSCoordinateBounds()
        mapDataState = .singleTruckFocus
        clusterManager.clearItems()
        searchButton.isHidden = true
        
        let fromLocation = CLLocationCoordinate2D(latitude: truck.lat, longitude: truck.long)
        let poItem = POIItem(position: fromLocation, data: truck)
        let marker = buildMarkerObj(poItem)
        marker.map = mapView
        trackingTruck = true
        filterButton.isHidden = true
        closeBtn.setImage(UIImage(named: "close"), for: .normal)
        
        let bottomVc = self.pulleyViewController?.drawerContentViewController as! TruckDetailsViewController
        bottomVc.setTruckDetails(truck: truck)
        self.pulleyViewController?.setDrawerPosition(position: .collapsed, animated: false)
        
        mapView.moveCamera(GMSCameraUpdate.setTarget(fromLocation))
        mapView.animate(toZoom: 15)
        
        if truck.deliveryStation != nil && truck.deliveryStation!.lat > 0 {
            addDestinationMarker(truck: truck)
             networkUtil.getPolyline(origin: "\(truck.lat),\(truck.long)", destination: "\(truck.deliveryStation!.lat),\(truck.deliveryStation!.long)", onCompleted: { result in
                 if(!result.isEmpty) {
                     self.mapView.drawPath(result)
                 }
             })
         }
        
        singleDocListener = firebaseColRef.document(truck.regNumber).addSnapshotListener { ( snapshot: DocumentSnapshot?, err) in
            if err != nil || snapshot == nil{
                return
            }
            if let truck = TruckModel(data: snapshot!.data()!)?.truck {
                self.animateTruckOnMap(truck: truck)
            }
        }
    }

    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
