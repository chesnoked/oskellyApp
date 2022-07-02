//
// Created by Виталий Хлудеев on 06.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class D3SecureController: UIViewController, UIWebViewDelegate {

    var webView = UIWebView(frame: UIScreen.main.bounds)

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Подтверждение платежа"
        webView.delegate = self
        view.addSubview(webView)

        let current = GlobalProvider.instance.paymentRequestProvider.getCurrent()
        GlobalProvider.instance.getApiRequester().hold(paymentRequest: current, webView: self.webView, need3DSHandler: { d3SecureData in
            let paymentRequest = GlobalProvider.instance.paymentRequestProvider.getCurrent()
            paymentRequest.d3SecureData = d3SecureData
            GlobalProvider.instance.paymentRequestProvider.setCurrent(paymentRequest: paymentRequest)
        }, successHandler: { paymentStatus in
            self.showPaymentResultController(paymentStatus: paymentStatus)
        })
    }


    public func webViewDidFinishLoad(_ webView: UIWebView) {
        print(webView.stringByEvaluatingJavaScript(from: "document.documentElement.outerHTML"))
        print(webView.stringByEvaluatingJavaScript(from: "document.querySelectorAll('[name=\"PaRes\"]')"))
        let paRes = webView.stringByEvaluatingJavaScript(from: "document.querySelector('[name=\"PaRes\"]').value")
        let paymentRequest = GlobalProvider.instance.paymentRequestProvider.getCurrent()
        if(paRes != nil && paRes != "" && paymentRequest.d3SecureData != nil) {
            webView.isHidden = true
            GlobalProvider.instance.getApiRequester().confirmPayment(d3SecureData: paymentRequest.d3SecureData!, paRes: paRes!, completeHandler: {paymentStatus in
                self.showPaymentResultController(paymentStatus: paymentStatus)
            })
        }
    }

    func showPaymentResultController(paymentStatus: String) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        var c = storyboard.instantiateViewController(withIdentifier: "PaymentResultController") as! PaymentResultController
        c.paymentStatus = paymentStatus
        self.navigationController?.pushViewController(c, animated: true)
    }
}