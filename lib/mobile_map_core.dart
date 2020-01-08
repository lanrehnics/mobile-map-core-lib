import 'dart:async';

import 'package:flutter/services.dart';

class MobileMapCore {
  static const MethodChannel _channel = const MethodChannel('mobile_map_core');

  static Future<String> prepareMap(
      {String url, String appType, String authToken, String id}) async {
    final List<dynamic> args = <dynamic>[url, appType, authToken, id];
    final String res = await _channel.invokeMethod('prepareMap', args);
    return res;
  }
}
