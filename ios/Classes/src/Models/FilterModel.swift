//
//  FilterModel.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 01/11/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation

struct FilterModel {
    
    var positioned = false
    var inPremise = false
    var loaded = false
    var atDestination = false
    var availableTruck = false
    var flagged = false
    var customerLocations = false
    var koboStations = false
    
}

enum FilterOption {
    case positioned, inPremise, loaded, atDestination, flagged, customerLocations, koboStations
}
