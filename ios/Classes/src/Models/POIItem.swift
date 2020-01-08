//
//  POIItem.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 05/11/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation

class POIItem: NSObject, GMUClusterItem {
    var position: CLLocationCoordinate2D
    var data: Truck!
    var marker: GMSMarker?
    
    init(position: CLLocationCoordinate2D, data: Truck) {
        self.position = position
        self.data = data
    }
}

enum MarkerType {
    case truck, customerLocation, koboStation
}
