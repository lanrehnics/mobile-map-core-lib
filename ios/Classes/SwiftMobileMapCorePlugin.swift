import Flutter
import UIKit
import GoogleMaps
import Pulley

public class SwiftMobileMapCorePlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "mobile_map_core", binaryMessenger: registrar.messenger())
    let instance = SwiftMobileMapCorePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    
    if call.method == "launch_map" {
        let mainContentVC = MapViewController()
        let platformArgs = call.arguments as! [String: Any]
        let model = ConfigModel(userType: UserType(rawValue: platformArgs["app_type"] as! String)!,
                                authToken: platformArgs["token"] as! String,
                                userTypeId: platformArgs["id"] as! Int)
        print("config model:", model)
        mainContentVC.setConfigurationModel(model: model)
        let bottomVC = TruckDetailsViewController()
        let pulleyVc = PulleyViewController(contentViewController: mainContentVC, drawerViewController: bottomVC)
        pulleyVc.modalPresentationStyle = .overFullScreen
        UIApplication.shared.keyWindow?.rootViewController?.present(pulleyVc, animated: true, completion: nil)
    } else {
        result("iOS " + UIDevice.current.systemVersion)
    }
  }
}
