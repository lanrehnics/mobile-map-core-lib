import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mobile_map_core/mobile_map_core.dart';

void main() {
  const MethodChannel channel = MethodChannel('mobile_map_core');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MobileMapCore.prepareMap, '42');
  });
}
