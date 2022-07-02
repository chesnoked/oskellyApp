//
// Created by Виталий Хлудеев on 17.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class HomePageFooterViewCell : UICollectionViewCell {

    private let button = WhiteButton()
    private let minWidth = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)
    private var controller : UIViewController!

    override init(frame: CGRect) {
        super.init(frame: frame)
        button.setTitle("Смотреть все новинки", for: .normal)
        self.contentView.addSubview(button)
        button.snp.makeConstraints { (make) -> Void in
            make.center.equalTo(self.snp.center)
            make.width.equalTo(minWidth * 0.90)
        }
        button.addTarget(self, action: #selector(self.buttonClicked), for: .touchUpInside)
    }

    func render(controller : UIViewController) {
        self.controller = controller
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func buttonClicked() {
        if (!GlobalProvider.instance.getAccountManager().isUserLoggedIn()) {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "RegistrationViewController") as! RegistrationViewController
            self.controller.present(vc, animated: true, completion: nil)
            return
        }
        let c = controller.storyboard?.instantiateViewController(withIdentifier: "CatalogProductListController") as! CatalogProductListController
        c.request.sort = "publishtime_desc"
        c.catalogName = "Новинки"
        controller.navigationController?.pushViewController(c, animated: true)
    }
}
