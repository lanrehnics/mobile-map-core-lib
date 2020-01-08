//
//  FilterViewController.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 24/11/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import UIKit

class FilterViewController: UIViewController {
    
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    @IBOutlet weak var positionedBox: CheckBox!
    @IBOutlet weak var positionedView: UILabel!
    @IBOutlet weak var inpremiseBox: CheckBox!
    @IBOutlet weak var inPremiseView: UILabel!
    @IBOutlet weak var loadedBox: CheckBox!
    @IBOutlet weak var loadedView: UILabel!
    @IBOutlet weak var atDestinationBox: CheckBox!
    @IBOutlet weak var atdestinationView: UILabel!
    @IBOutlet weak var flaggedBox: CheckBox!
    @IBOutlet weak var flaggedView: UILabel!
    @IBOutlet weak var availableTruckBox: CheckBox!
    @IBOutlet weak var availableTruckView: UILabel!
    @IBOutlet weak var customerLocBox: CheckBox!
    @IBOutlet weak var custLocView: UILabel!
    @IBOutlet weak var koboStationsBox: CheckBox!
    @IBOutlet weak var koboStationsView: UILabel!
    @IBOutlet weak var closeBtn: UIButton!
    
    
    @IBOutlet weak var positionedStack: UIStackView!
    @IBOutlet weak var loadedStack: UIStackView!
    @IBOutlet weak var atDestinationStack: UIStackView!
    @IBOutlet weak var flaggedStack: UIStackView!
    @IBOutlet weak var availableStack: UIStackView!
    @IBOutlet weak var customerLocStack: UIStackView!
    @IBOutlet weak var koboStationsStack: UIStackView!
    @IBOutlet weak var inpremiseStack: UIStackView!
    
    var filterModel: FilterModel!
    let fullView: CGFloat = 100
    var partialView: CGFloat {
        return UIScreen.main.bounds.height / 2
    }
    
    func setFilterModel(model: FilterModel) {
        self.filterModel = model
        positionedBox.isChecked = model.positioned
        koboStationsBox.isChecked = model.koboStations
        customerLocBox.isChecked = model.customerLocations
        availableTruckBox.isChecked = model.availableTruck
        atDestinationBox.isChecked = model.atDestination
        loadedBox.isChecked = model.loaded
        flaggedBox.isChecked = model.flagged
        inpremiseBox.isChecked = model.inPremise
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        positionedBox.style = .tick
        positionedBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        positionedBox.borderStyle = .square
        koboStationsBox.style = .tick
        var gesture = UITapGestureRecognizer(target: self, action: #selector(onfilterOptionTapped(_:)))
        positionedView.isUserInteractionEnabled = true
        positionedView.addGestureRecognizer(gesture)

        koboStationsBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        koboStationsBox.borderStyle = .square
        customerLocBox.style = .tick
        customerLocBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        customerLocBox.borderStyle = .square
        availableTruckBox.style = .tick
        availableTruckBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        availableTruckBox.borderStyle = .square
        atDestinationBox.style = .tick
        atDestinationBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        atDestinationBox.borderStyle = .square
        loadedBox.style = .tick
        loadedBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        loadedBox.borderStyle = .square
        flaggedBox.style = .tick
        flaggedBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        flaggedBox.borderStyle = .square

        inpremiseBox.style = .tick
        inpremiseBox.addTarget(self, action: #selector(onCheckBoxValueChanged(_:)), for: .valueChanged)
        inpremiseBox.borderStyle = .square
        gesture = UITapGestureRecognizer(target: self, action: #selector(onfilterOptionTapped(_:)))
        inPremiseView.isUserInteractionEnabled = true
        inPremiseView.addGestureRecognizer(gesture)

        customerLocStack.isHidden = true
        koboStationsStack.isHidden = true
        flaggedStack.isHidden = true

//        custLocView.isHidden = true
//        koboStationsView.isHidden = true
//        flaggedView.isHidden = true
//        customerLocBox.isHidden = true
//        koboStationsBox.isHidden = true
//        flaggedBox.isHidden = true

//        let closeTap = UITapGestureRecognizer(target: self, action: #selector(closeModal))
//        closeBtn.isUserInteractionEnabled = true
//        closeBtn.addGestureRecognizer(closeTap)
                
        view.layer.cornerRadius = 5
        view.clipsToBounds = true
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        prepareBackgroundView()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        UIView.animate(withDuration: 0.6, animations: { [weak self] in
            let frame = self?.view.frame
            let yComponent = self?.partialView
            self?.view.frame = CGRect(x: 0, y: yComponent!, width: frame!.width, height: frame!.height)
        })
    }

    @IBAction func onCloseBtnClicked(_ sender: Any) {
        UIView.animate(withDuration: 0.5, animations: {
            let frame = self.view.frame
            
            self.view.frame = CGRect(x: 0, y: frame.maxY - 100, width: frame.width, height: 0)
        })
    }
    
    @IBAction func tabChanged(_ sender: Any) {
        switch segmentedControl.selectedSegmentIndex {
        case 0:
            positionedStack.isHidden = false
            inpremiseStack.isHidden = false
            loadedStack.isHidden = false
            atDestinationStack.isHidden = false
            availableStack.isHidden = false
            
            customerLocStack.isHidden = true
            koboStationsStack.isHidden = true
            flaggedStack.isHidden = true
        default:
            positionedStack.isHidden = true
            inpremiseStack.isHidden = true
            loadedStack.isHidden = true
            atDestinationStack.isHidden = true
            availableStack.isHidden = true
            
            customerLocStack.isHidden = false
            koboStationsStack.isHidden = false
            flaggedStack.isHidden = false
            break
        }
    }
    
    
    @IBAction func clearFilters(_ sender: Any) {
        filterModel.positioned = false
        filterModel.inPremise = false
        filterModel.loaded = false
        filterModel.atDestination = false
        filterModel.availableTruck = false
        filterModel.flagged = false
        filterModel.customerLocations = false
        filterModel.koboStations = false
        let parentVc = self.parent as! MapViewController
        parentVc.clearFilters(model: filterModel)
        closeModal()
    }
    
    func closeModal() {
       UIView.animate(withDuration: 0.5, animations: {
            let frame = self.view.frame
            
            self.view.frame = CGRect(x: 0, y: frame.maxY - 100, width: frame.width, height: 0)
        })
    }
    
    @objc func onCheckBoxValueChanged(_ sender: CheckBox) {
        print("checkbox checked")
        if sender == positionedBox {
            print("it is positioned Box")
        }
        switch sender {
        case positionedBox:
            filterModel.positioned = sender.isChecked
        case inpremiseBox:
            filterModel.inPremise = sender.isChecked
        case loadedBox:
            filterModel.loaded = sender.isChecked
        case atDestinationBox:
            filterModel.atDestination = sender.isChecked
        case availableTruckBox:
            filterModel.availableTruck = sender.isChecked
        case flaggedBox:
            filterModel.flagged = sender.isChecked
        case customerLocBox:
            filterModel.customerLocations = sender.isChecked
        case koboStationsBox:
            filterModel.koboStations = sender.isChecked
        default:
            print("default")
        }
    }
    
    @objc func onfilterOptionTapped(_ option: UIStackView) {
        print(option)
        switch option {
        case positionedView:
            filterModel.positioned = !filterModel.positioned
            positionedBox.isChecked = !positionedBox.isChecked
        case inPremiseView:
            filterModel.inPremise = !filterModel.inPremise
            inpremiseBox.isChecked = !inpremiseBox.isChecked
        default:
            print("none")
        }
    }
    
    func prepareBackgroundView(){
        let blurEffect = UIBlurEffect.init(style: .light)
        let visualEffect = UIVisualEffectView.init(effect: blurEffect)
        let bluredView = UIVisualEffectView.init(effect: blurEffect)
        bluredView.contentView.addSubview(visualEffect)
        
        visualEffect.frame = UIScreen.main.bounds
        bluredView.frame = UIScreen.main.bounds
        
        view.insertSubview(bluredView, at: 0)
    }
    
    
    @IBAction func submitBtn(_ sender: Any) {
        let parentVc = self.parent as! MapViewController
        parentVc.applyFilters(model: filterModel)
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
