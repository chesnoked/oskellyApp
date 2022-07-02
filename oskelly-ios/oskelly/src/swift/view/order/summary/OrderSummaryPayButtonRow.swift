//
// Created by Виталий Хлудеев on 24.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderSummaryPayButtonRow: UITableViewCell {

    private let btn = DarkButton()
    private var controller: UIViewController?

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        isUserInteractionEnabled = true

        contentView.addSubview(btn)
        btn.setTitle("Перейти к оплате", for: .normal)
        btn.snp.makeConstraints({ m in
            m.edges.equalTo(contentView).inset(UIEdgeInsetsMake(10, 10, 10, 10))
            m.height.equalTo(50)
        })
        btn.addTarget(self, action: #selector(self.click), for: .touchUpInside)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(controller: UIViewController) {
        self.controller = controller
    }

    func click() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        var c = storyboard.instantiateViewController(withIdentifier: "CreditCardController") as! CreditCardController
        controller?.navigationController?.pushViewController(c, animated: true)
    }
}
