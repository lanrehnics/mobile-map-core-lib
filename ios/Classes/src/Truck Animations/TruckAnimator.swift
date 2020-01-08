//
//  TruckAnimator.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 29/10/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation
import GoogleMaps

struct TruckAnimator {
    
    let truckMarker: GMSMarker
    let mapView: GMSMapView
    
    func animate(from source: CLLocationCoordinate2D, to destination: CLLocationCoordinate2D, bearing: Double) {
        
        CATransaction.begin()
        CATransaction.setAnimationDuration(20)
        CATransaction.setCompletionBlock({
            // later implement
        })
        truckMarker.rotation = bearing
        truckMarker.groundAnchor = CGPoint(x: CGFloat(0.5), y: CGFloat(0.5))
        CATransaction.commit()
        
        CATransaction.begin()
        CATransaction.setAnimationDuration(20)
        truckMarker.position = destination
        
        //let camera = GMSCameraUpdate.setTarget(destination, zoom: 16)
        //mapView.animate(with: camera)
    
        CATransaction.commit()
    }
}
