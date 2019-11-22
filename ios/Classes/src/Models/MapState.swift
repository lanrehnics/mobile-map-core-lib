//
//  MapState.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 05/11/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation

/*
 Holds the current state of the map
 */
enum MapDataState {
    
    case initial, filtering, searching, singleTruckFocus
}

enum DocumentState {
    case entered, moved, exited
}
