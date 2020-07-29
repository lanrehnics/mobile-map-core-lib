import UIKit
import Flutter
import GoogleMaps

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    GMSServices.provideAPIKey("AIzaSyCZsmroa6P94FfCXaWIlVd9PcVpVpQYdqs")
   
    super.application(application, didFinishLaunchingWithOptions: launchOptions)
    
    let mainVc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "DammyViewController")
    
    window?.rootViewController = mainVc
    window?.makeKeyAndVisible()
    
    return true;
  }
}
