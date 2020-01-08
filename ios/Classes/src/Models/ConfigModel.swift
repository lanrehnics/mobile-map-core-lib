//
//  ConfigModel.swift
//  Runner
//
//  Created by Adedamola Adeyemo on 24/12/2019.
//  Copyright © 2019 The Chromium Authors. All rights reserved.
//

import Foundation

struct ConfigModel {
    var userType: UserType
    var authToken: String
    var koboStationsUrl: String
    var customer: String
    var userTypeId: Int
}

enum UserType: String {
    case customer = "customer"
    case partner = "partner"
    case squad = "squad"
}
