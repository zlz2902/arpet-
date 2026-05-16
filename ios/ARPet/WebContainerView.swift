import AVFoundation
import SwiftUI
import WebKit

/// 与 Android 版一致：先加载主页 index.html，用户点击后再进入 ar_mobile.html。
struct WebContainerView: UIViewRepresentable {

  func makeCoordinator() -> Coordinator {
    Coordinator()
  }

  func makeUIView(context: Context) -> WKWebView {
    let configuration = WKWebViewConfiguration()
    configuration.allowsInlineMediaPlayback = true
    configuration.mediaTypesRequiringUserActionForPlayback = []

    let webView = WKWebView(frame: .zero, configuration: configuration)
    webView.navigationDelegate = context.coordinator
    webView.uiDelegate = context.coordinator
    webView.isOpaque = false
    webView.backgroundColor = .black
    webView.scrollView.bounces = false

    context.coordinator.loadHome(in: webView)
    return webView
  }

  func updateUIView(_ uiView: WKWebView, context: Context) {}

  final class Coordinator: NSObject, WKNavigationDelegate, WKUIDelegate {
    private var wwwDirectory: URL? {
      Bundle.main.resourceURL?.appendingPathComponent("www", isDirectory: true)
    }

    func loadHome(in webView: WKWebView) {
      guard let www = wwwDirectory else { return }
      let index = www.appendingPathComponent("index.html")
      webView.loadFileURL(index, allowingReadAccessTo: www)
    }

    func webView(
      _ webView: WKWebView,
      decidePolicyFor navigationAction: WKNavigationAction,
      decisionHandler: @escaping (WKNavigationActionPolicy) -> Void
    ) {
      guard let url = navigationAction.request.url,
        url.lastPathComponent == "ar_mobile.html"
      else {
        decisionHandler(.allow)
        return
      }

      switch AVCaptureDevice.authorizationStatus(for: .video) {
      case .authorized:
        decisionHandler(.allow)
      case .notDetermined:
        AVCaptureDevice.requestAccess(for: .video) { granted in
          DispatchQueue.main.async {
            decisionHandler(granted ? .allow : .cancel)
          }
        }
      default:
        decisionHandler(.cancel)
      }
    }

    @available(iOS 15.0, *)
    func webView(
      _ webView: WKWebView,
      requestMediaCapturePermissionFor origin: WKSecurityOrigin,
      initiatedByFrame frame: WKFrameInfo,
      type: WKMediaCaptureType,
      decisionHandler: @escaping (WKPermissionDecision) -> Void
    ) {
      decisionHandler(.grant)
    }
  }
}
