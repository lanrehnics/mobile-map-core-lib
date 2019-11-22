//
//  SearchResultTableViewCell.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 04/11/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import UIKit

class SearchResultTableViewCell: UITableViewCell {
    @IBOutlet weak var resultLabel: UILabel!
    @IBOutlet weak var resultTypeView: UIView!
    @IBOutlet weak var resultTypeLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setRowData(_ data: SearchResultModel) {
        resultLabel.text = data.result
        switch data.type {
        case .truck:
            resultTypeLabel.text = "Truck"
        case .address:
            resultTypeLabel.text = "Area / Region"
        case .customerLocations:
            resultTypeLabel.text = "Customer Location"
        default :
            print("stuff")
        }
    }
    
}
