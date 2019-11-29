//
//  SearchViewController.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 04/11/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import UIKit
import FirebaseFirestore
import GoogleMaps

class SearchViewController: UIViewController, UITableViewDelegate, UISearchBarDelegate, UITableViewDataSource {
    
    var mapVC: MapViewController?
    
    
    @IBOutlet weak var searchBar: UISearchBar!
    
    var indicator = UIActivityIndicatorView()
    
    var searchData = [SearchResultModel]()
    
    @IBOutlet weak var tbl: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tbl.delegate = self
        tbl.dataSource = self
        tbl.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        tbl.tableFooterView = UIView()
        searchData = []
        searchBar.delegate = self
        self.registerTableViewCells()
        buildActivityIndicator()
        // Do any additional setup after loading the view.
    }
    
    func buildActivityIndicator() {
        indicator.frame = CGRect(x: 0, y: 0, width: 40, height: 40)
        indicator.center = self.view.center
        self.view.addSubview(indicator)
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        print("search count", searchData.count)
        return searchData.count
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        if let searchTerm = searchBar.text {
            searchData = []
            indicator.startAnimating()
            searchByTruckRegNum(searchTerm)
            searchByAddress(searchTerm)
            searchBar.resignFirstResponder()
        }
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tbl.dequeueReusableCell(withIdentifier: "cell") as! SearchResultTableViewCell
        cell.setRowData(searchData[indexPath.row])
        return cell
    }
    
    func registerTableViewCells() {
        let cell = UINib(nibName: "SearchResultTableViewCell", bundle: nil)
        self.tbl.register(cell, forCellReuseIdentifier: "cell")
    }
    
    func searchByTruckRegNum(_ regNum: String) {
        print("connect to firebase search")
        Firestore.firestore().collection("Trucks").whereField("d.reg_number", isEqualTo: regNum).getDocuments {
             (snapShot: QuerySnapshot?, err) in
             print(" search result is back")
            if err != nil || snapShot == nil{
                print("an error has occured", err?.localizedDescription ?? "error")
                return
            }
            print("data is back", snapShot?.documents.count ?? 0)
            for document in snapShot!.documents {
                let truckModel = TruckModel(data: document.data())
                if let truck = truckModel?.truck {
                    let data = SearchResultModel(type: .truck, result: truck.regNumber, data: truck)
                    self.searchData.append(data)
                }
            }
            self.tbl.reloadData()
        }
    }
    
    func searchByAddress(_ address: String) {
        let geoCoder = CLGeocoder()
        geoCoder.geocodeAddressString(address) {
            (placeMarks, error) in
            print("geocode result is back")
            if error != nil || placeMarks == nil {
                self.stopLoadingAnimations()
                print("error occured while geocoding --- ", error?.localizedDescription ?? "error")
                return
            }
            print("placemarks here --- ", placeMarks?.count ?? 0)
            for placemark in placeMarks! {
                let data = SearchResultModel(type: .address, result:
                    "\(placemark.subThoroughfare ?? "") \(placemark.administrativeArea ?? "") \(placemark.country ?? "")", data: placemark)
                self.searchData.append(data)
            }
            self.stopLoadingAnimations()
        }
    }
    
    func stopLoadingAnimations() {
        self.tbl.reloadData()
        self.indicator.stopAnimating()
        self.indicator.hidesWhenStopped = true
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let data = searchData[indexPath.row]
        mapVC?.search(searchModel: data)
        dismiss(animated: true, completion: nil)
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
