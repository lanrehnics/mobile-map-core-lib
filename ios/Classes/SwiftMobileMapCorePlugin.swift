import Flutter
import UIKit
import GoogleMaps
import Pulley
import BattleFieldIOS


public class SwiftMobileMapCorePlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "mobile_map_core", binaryMessenger: registrar.messenger())
        let instance = SwiftMobileMapCorePlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }   
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let platformArgs = call.arguments as! [String: Any]
        if call.method == "launch_map" {
            let model = UserConfigModel(
                userType: platformArgs["app_type"] as! String, userTypeId: platformArgs["id"] as! Int, token: platformArgs["token"] as! String, userName: platformArgs["user_name"] as! String, koboUrl: platformArgs["kobo_base_url"] as! String,
                gpsBaseUrl: platformArgs["gps_base_url"] as! String
            )
             print("config model:", model)
            SessionManager.saveUserDetails(model: model)
            loadViewController(model)
        } else if call.method == "navigate_driver" {
            let regNumber = platformArgs["reg_number"] as! String
            let destination = platformArgs["destination"] as? [Double]
            let vc = DriverNavigationVC()
            vc.dest = destination
            vc.regNumber = regNumber
            vc.modalPresentationStyle = .fullScreen
           UIApplication.shared.keyWindow?.rootViewController?.present(vc, animated: true, completion: nil)
        } else {
            result("iOS " + UIDevice.current.systemVersion)
        }
    }
    
    func loadViewController(_ userConfig: UserConfigModel) {
        let dataProvider = MapDataProvider()
        let mainContentVc = MapViewController()
        mainContentVc.dataProvider = dataProvider
        mainContentVc.configModel = userConfig;
        let bottomVc = UserConfigViewController()
        bottomVc.userConfig = userConfig
        bottomVc.dataProvider = dataProvider
        let pulleyVc = PulleyViewController(contentViewController: mainContentVc, drawerViewController: bottomVc)
        let navController = UINavigationController(rootViewController: pulleyVc)
        navController.modalPresentationStyle = .overFullScreen
        UIApplication.shared.keyWindow?.rootViewController?.present(navController, animated: true, completion: nil)
    }
}
