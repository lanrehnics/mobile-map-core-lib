//
//  Extensions.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 29/10/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import GoogleMaps

extension GMSMapView {
    
    func updateMap(toLocation location: CLLocation, zoomLevel: Float? = nil) {
        if let zoomLevel = zoomLevel {
            let cameraUpdate = GMSCameraUpdate.setTarget(location.coordinate, zoom: zoomLevel)
            animate(with: cameraUpdate)
        } else {
            animate(toLocation: location.coordinate)
        }
    }
    
    func drawPath(_ encodedPathString: String) {
        CATransaction.begin()
        CATransaction.setAnimationDuration(0.0)
        let path = GMSPath(fromEncodedPath: encodedPathString)
        let line = GMSPolyline(path: path)
        line.strokeWidth = 4.0
        line.strokeColor = UIColor.routeColor
        line.isTappable = true
        line.map = self
        CATransaction.commit()
    }
}

extension UIColor {
    static var routeColor: UIColor {
        let color: UIColor
        if #available(iOS 11.0, *) {
            color = UIColor( named: "routeColor")!
        } else {
            color = UIColor(red:1.00, green:0.00, blue:0.60, alpha:1.0)
        }
        return color
    }
}

extension CLLocationCoordinate2D {
    
    func bearing(to point: CLLocationCoordinate2D) -> Double {
        func degreesToRadians(_ degrees: Double) -> Double { return degrees * Double.pi / 180.0 }
        func radiansToDegrees(_ radians: Double) -> Double { return radians * 180.0 / Double.pi }
        
        let fromLatitude = degreesToRadians(latitude)
        let fromLongitude = degreesToRadians(longitude)
        
        let toLatitude = degreesToRadians(point.latitude)
        let toLongitude = degreesToRadians(point.longitude)
        
        let differenceLongitude = toLongitude - fromLongitude
        
        let y = sin(differenceLongitude) * cos(toLatitude)
        let x = cos(fromLatitude) * sin(toLatitude) - sin(fromLatitude) * cos(toLatitude) * cos(differenceLongitude)
        let radiansBearing = atan2(y, x);
        let degree = radiansToDegrees(radiansBearing)
        return (degree >= 0) ? degree : (360 + degree)
    }
}

extension CLLocationCoordinate2D : Equatable{
    public static func == (lhs: CLLocationCoordinate2D, rhs: CLLocationCoordinate2D) -> Bool {
        return lhs.latitude == rhs.latitude && lhs.longitude == rhs.longitude
    }
    //distance in meters, as explained in CLLoactionDistance definition
    func distance(from: CLLocationCoordinate2D) -> CLLocationDistance {
        let destination=CLLocation(latitude:from.latitude,longitude:from.longitude)
        return CLLocation(latitude: latitude, longitude: longitude).distance(from: destination)
    }
}
