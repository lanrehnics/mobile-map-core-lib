//
//  HubLocations.swift
//  Runner
//
//  Created by Adedamola Adeyemo on 06/01/2020.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation

struct HubLocation {
    var hubs: Array<Hub> = Array()
    
    init?(data: [String: Any]) {
        if let hubData = data["locations"] as? [[String: Any]] {
            for hub in hubData {
                self.hubs.append(Hub(hub))
            }
        } else {
            print("not an array")
        }
    }
}

struct Hub {
    var id: Int
    var name: String
    var address: String
    var state: String
    var lat: String
    var long: String
    var contact_name: String
    var contact_phone: String
    var geo_hash: String
    var radius: Int
    
    init(_ data: [String: Any]) {
        id = data["id"] as? Int ?? 0
        name = data["name"] as? String ?? ""
        address = data["address"] as? String ?? ""
        lat = data["lat"] as? String ?? ""
        long = data["long"] as? String ?? ""
        state = data["state"] as? String ?? ""
        contact_name = data["contact_name"] as? String ?? ""
        contact_phone = data["contact_phone"] as? String ?? ""
        geo_hash = data["geo_hash"] as? String ?? ""
        radius = data["radius"] as? Int ?? 0
    }
}
