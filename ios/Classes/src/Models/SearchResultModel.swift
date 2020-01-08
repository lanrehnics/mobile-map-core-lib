//
//  SearchResultModel.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 04/11/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation

struct SearchResultModel {
    var type: SearchResultType!
    var result: String!
    var data: Any
}

enum SearchResultType {
    case truck, customerLocations, address
}
