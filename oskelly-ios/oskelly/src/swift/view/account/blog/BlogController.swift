//
// Created by Виталий Хлудеев on 08.02.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class BlogController : UIViewController, UIWebViewDelegate {

    var webView = UIWebView(frame: UIScreen.main.bounds)

    override func viewDidLoad() {
        super.viewDidLoad()
        GlobalProvider.instance.cartIconService.addCartIcon(self)
        navigationItem.title = "Блог"
        webView.delegate = self
        view.addSubview(webView)
        let url = URL (string: "https://oskelly.ru/blog?webView=true");
        let request = URLRequest(url: url!);
        webView.loadRequest(request);
    }
}