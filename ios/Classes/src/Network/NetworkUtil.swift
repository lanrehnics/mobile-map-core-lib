//
//  NetworkUtil.swift
//  kobo-ios-map
//
//  Created by Adedamola Adeyemo on 30/10/2019.
//  Copyright © 2019 Adedamola Adeyemo. All rights reserved.
//

import Foundation

struct NetworkUtil {
    
    var session = URLSession.shared
    var directionsBaseUrl = "https://maps.googleapis.com/maps/api/directions/json?"

    
    func getPolyline(origin: String, destination: String, onCompleted: @escaping (String)->Void) {
        var polyline = "";
        let url = URL(string: "\(directionsBaseUrl)origin=\(origin)&destination=\(destination)&mode=driving&key=AIzaSyCZsmroa6P94FfCXaWIlVd9PcVpVpQYdqs")!
        print(url)
        let task = session.dataTask(with: url){ data, response, error in
            
            if error != nil && data == nil {
                print("client error")
                polyline = ""
                return
            }
            
            guard let response = response as? HTTPURLResponse, (200...300).contains(response.statusCode) else {
                print("response error")
                return
            }
            
            do {
                let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String: Any]
                let status = json["status"] as? String ?? ""
                if status.lowercased() == "ok" {
                
                let routes = json["routes"] as! [[String: Any]]
                let overview = routes[0]["overview_polyline"] as! [String: Any]
                polyline = overview["points"] as! String
                onCompleted(polyline)
                }
            } catch {
                print("JSON error: \(error.localizedDescription)")
            }
        }
        task.resume()
    }
}
