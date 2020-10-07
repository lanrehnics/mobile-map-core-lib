import Flutter
import UIKit
import GoogleMaps
import Pulley
import BattleFieldIOS
import SideMenuSwift


public class SwiftMobileMapCorePlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "mobile_map_core", binaryMessenger: registrar.messenger())
        let instance = SwiftMobileMapCorePlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }   
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let platformArgs = call.arguments as! [String: Any]
        if call.method == "launch_map" {
            let userType = platformArgs["app_type"] as! String
            let model = UserConfigModel(
                userType: userType, userTypeId: platformArgs["id"] as! Int, token: platformArgs["token"] as! String, userName: platformArgs["user_name"] as! String, koboUrl: platformArgs["kobo_base_url"] as! String,
                gpsBaseUrl: platformArgs["geo_base_url"] as! String, environment: platformArgs["environment"] as! String
            )
             print("config model:", model)
            SessionManager.saveUserDetails(model: model)
            loadViewController(model, userType: userType)
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
    
    func loadViewController(_ userConfig: UserConfigModel, userType: String) {
        if userType.lowercased() == "admin" {
            let adminHome = ActiveTripVC()
             let menuVC = SlideTransitionViewController()
             let controller = SideMenuController(contentViewController: adminHome, menuViewController: menuVC)
             SideMenuController.preferences.basic.menuWidth = 260
             SideMenuController.preferences.basic.position = .above
             SideMenuController.preferences.basic.direction = .left
             SideMenuController.preferences.basic.enablePanGesture = true
             SideMenuController.preferences.basic.supportedOrientations = .portrait
             SideMenuController.preferences.basic.shouldRespectLanguageDirection = true
             let navController = UINavigationController(rootViewController: controller)
             navController.modalPresentationStyle = .overFullScreen
            UIApplication.shared.keyWindow?.rootViewController?.present(navController, animated: true, completion: nil)
        } else {
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
}
