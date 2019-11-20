#import "MobileMapCorePlugin.h"
#import <mobile_map_core/mobile_map_core-Swift.h>

@implementation MobileMapCorePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMobileMapCorePlugin registerWithRegistrar:registrar];
}
@end
