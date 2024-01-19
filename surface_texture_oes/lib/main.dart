import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final id = await SurfaceTextureOESPlugin.generateSurfaceTexture();
  runApp(_MyApp(id!));
}

final class SurfaceTextureOESPlugin {
  static const _channel = MethodChannel('surface_texture_oes');

  static Future<int?> generateSurfaceTexture() async {
    return await _channel.invokeMethod('generateSurfaceTexture');
  }
}

final class _MyApp extends StatelessWidget {
  final int _textureId;

  const _MyApp(this._textureId);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Texture(
            textureId: _textureId,
          ),
        ),
      ),
    );
  }
}
