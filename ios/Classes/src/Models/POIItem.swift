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
    var markerType: MarkerType!
    
    init(position: CLLocationCoordinate2D, data: Truck, type: MarkerType) {
        self.position = position
        self.data = data
        self.markerType = type
    }
}

enum MarkerType {
    case truck, customerLocation, koboStation
}
