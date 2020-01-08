//
//  TruckModel.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 29/10/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation

struct TruckModel {
    
    var truck: Truck?
    var g: String
    
    init?(data: [String: Any]) {
        self.truck = Truck(data: data["d"] as! [String : Any])
        self.g = data["g"] as? String ?? ""
    }
}
