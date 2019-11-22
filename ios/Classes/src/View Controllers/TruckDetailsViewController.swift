//
//  TruckDetailsViewController.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 21/10/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import UIKit
import Pulley

class TruckDetailsViewController: UIViewController {
    
    @IBOutlet weak var etaLabel: UILabel!
    @IBOutlet weak var headerViewCon: UIView!
    @IBOutlet weak var tripIdLabel: UILabel!
    @IBOutlet weak var statusLabel: UILabel!
    @IBOutlet weak var destLabel: UILabel!

    @IBOutlet weak var customerDetailsCard: UIView!
    
    @IBOutlet weak var driverMobileLabel: UILabel!

    @IBOutlet weak var truckRegLabel: UILabel!
    @IBOutlet weak var driverNameLabel: UILabel!
    @IBOutlet weak var pickUpLabel: UILabel!
    @IBOutlet weak var customerName: UILabel!
    @IBOutlet weak var driverDetailsCard: UIView!
    
    private var truck: Truck!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.isUserInteractionEnabled = true
        customerDetailsCard.layer.cornerRadius = 20.0
        customerDetailsCard.layer.shadowColor = UIColor.gray.cgColor
        customerDetailsCard.layer.shadowRadius = 6.0
        customerDetailsCard.layer.shadowOpacity = 0.5
        customerDetailsCard.layer.shadowOffset = CGSize(width: 0.0, height: 0.0)

        driverDetailsCard.layer.cornerRadius = 20.0
        driverDetailsCard.layer.shadowColor = UIColor.gray.cgColor
        driverDetailsCard.layer.shadowRadius = 6.0
        driverDetailsCard.layer.shadowOpacity = 0.5
        driverDetailsCard.layer.shadowOffset = CGSize(width: 0.0, height: 0.0)
        let headerGesture = UITapGestureRecognizer(target: self, action: #selector(handleHeaderTap))
        headerViewCon.isUserInteractionEnabled = true
        headerViewCon.addGestureRecognizer(headerGesture)
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.pulleyViewController?.drawerTopInset = 260
        if #available(iOS 10.0, *)
        {
           let feedbackGenerator = UISelectionFeedbackGenerator()
           self.pulleyViewController?.feedbackGenerator = feedbackGenerator
        }
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
//         Timer.scheduledTimer(timeInterval: 0.5, target: self, selector: #selector(bounceDrawer), userInfo: nil, repeats: false)
    }
    
    @objc func handleHeaderTap() {
        if pulleyViewController?.drawerPosition == .open {
            pulleyViewController?.setDrawerPosition(position: .collapsed, animated: true)
        } else {
            pulleyViewController?.setDrawerPosition(position: .open, animated: true)
        }
    }
    
    @objc func panGesture(recognizer: UIPanGestureRecognizer) {
        let translation = recognizer.translation(in: self.view)
        let y = self.view.frame.minY
        self.view.frame = CGRect(x: 0, y: y + translation.y, width: view.frame.width, height: view.frame.height)
        recognizer.setTranslation(CGPoint.zero, in: self.view)
    }
    
    @objc fileprivate func bounceDrawer() {

        // We can 'bounce' the drawer to show users that the drawer needs their attention. There are optional parameters you can pass this method to control the bounce height and speed.
        //self.pulleyViewController?.bounceDrawer()
    }
    
    func roundViews() {
            view.layer.cornerRadius = 5
            view.clipsToBounds = true
    }
    
    func setTruckDetails(truck: Truck) {
        self.truck = truck
        if(truck.flagged) {
            headerViewCon.backgroundColor = UIColor(red:1.00, green:0.00, blue:0.00, alpha:1.0)
        } else {
            headerViewCon.backgroundColor = UIColor(red:0.21, green:0.70, blue:0.49, alpha:1.0)
        }
        tripIdLabel.text = truck.tripId
        statusLabel.text = truck.status
        customerName.text = truck.customerName
        pickUpLabel.text = "\(truck.source) \(truck.sourceCountry)"
        destLabel.text = truck.deliveryStation?.address ?? "N/A"
        driverNameLabel.text = truck.driverName
        driverMobileLabel.text = truck.driverMobile
        truckRegLabel.text = truck.regNumber
        etaLabel.text = "N/A"
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

extension TruckDetailsViewController: PulleyDrawerViewControllerDelegate {
    
    func collapsedDrawerHeight(bottomSafeArea: CGFloat) -> CGFloat
    {
        // For devices with a bottom safe area, we want to make our drawer taller. Your implementation may not want to do that. In that case, disregard the bottomSafeArea value.
        return 68.0 + (pulleyViewController?.currentDisplayMode == .drawer ? bottomSafeArea : 0.0)
    }
    
    func supportedDrawerPositions() -> [PulleyPosition] {
        return [.open, .collapsed, .closed]
    }
}
