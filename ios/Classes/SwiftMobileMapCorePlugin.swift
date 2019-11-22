import Flutter
import UIKit
import GoogleMaps

public class SwiftMobileMapCorePlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "mobile_map_core", binaryMessenger: registrar.messenger())
    let instance = SwiftMobileMapCorePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    var truck = Truck(data: ["" : ""])
    truck?.customerName = "Miss bimbo in the spirit"
    
    result("iOS " + UIDevice.current.systemVersion + " " + truck!.customerName)
  }
    
    func testStuff() {
       
    }
}
