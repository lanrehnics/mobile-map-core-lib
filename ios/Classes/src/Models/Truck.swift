//
//  Truck.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 29/10/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation

struct Truck {
    
    var active: Int
    var admin: Int
    var assetClass: String
    var bearing: String
    var customerId: Int
    var driverId: Int
    var partnerId: Int
    var speed: Double
    var source: String
    var status: String
    var tripId: String
    var transportStatus: String
    var sourceCountry: String
    var regNumber: String
    var customerName: String
    var recipient: String
    var driverMobile: String
    var driverName: String
    var destination: String
    var destinationCountry: String
    var delivered: Bool
    var flagged: Bool
    var long: Double
    var lat: Double
    var nextPosition: NextPosition?
    var deliveryStation: DeliveryStation?
    var isClustered: Bool

    init?(data: [String: Any]) {
        
        self.active = data["active"] as? Int ?? 0
        self.admin = data["admin"] as? Int ?? 0
        self.assetClass = data["asset_class"] as? String ?? "N/A"
        self.bearing = data["bearing"] as? String ?? "0"
        self.source =  data["source"] as? String ?? ""
        self.status = data["status"] as? String ?? ""
        self.tripId = data["tripId"] as? String ?? "N/A"
        self.transportStatus = data["transportStatus"] as? String ?? ""
        self.sourceCountry = data["sourceCountry"] as? String ?? ""
        self.regNumber = data["reg_number"] as? String ?? ""
        self.customerName = data["customerName"] as? String ?? ""
        self.recipient = data["recipient"] as? String ?? ""
        self.driverMobile = data["driverMobile"] as? String ?? ""
        self.driverName = data["driverName"] as? String ?? ""
        self.destination = data["destination"] as? String ?? ""
        self.destinationCountry = data["destinationCountry"] as? String ?? ""
        self.customerId = data["customerId"] as? Int ?? 0
        self.driverId = data["driverId"] as? Int ?? 0
        self.partnerId = data["partnerId"] as? Int ?? 0
        self.speed = data["speed"] as? Double ?? 0.0
        self.delivered = data["delivered"] as? Bool ?? false
        self.flagged = data["flagged"] as? Bool ?? false
        self.long = data["long"] as? Double ?? 0.0
        self.lat = data["lat"] as? Double ?? 0.0
        self.nextPosition = data["nextPosition"] != nil
            ? NextPosition(data: data["nextPosition"] as! [String : Any]) : nil
        if(data["deliveryStation"] != nil && !(data["deliveryStation"] is String)) {
            self.deliveryStation = DeliveryStation(data: data["deliveryStation"] as! [String : Any])
        }
        self.isClustered = true
    }

}

struct NextPosition {
    
    var long: Double
    var lat: Double
    
    init?(data: [String: Any]) {
        self.long = data["longitude"] as? Double ?? 0.0
        self.lat = data["latitude"] as? Double ?? 0.0
    }
}

struct DeliveryStation {
    
    var address: String
    var long: Double
    var lat: Double
    
    init?(data: [String: Any]) {
        self.address = data["address"] as? String ?? "N/A"
        let location = data["location"] as? [String: Any]
        let coordinate = location!["coordinates"] as? [Double] ?? nil
        if (coordinate != nil) {
            self.long = coordinate?[1] ?? 0.0
            self.lat = coordinate?[0] ?? 0.0
        } else {
            self.long = 0.0
            self.lat = 0.0
        }
    }
}
