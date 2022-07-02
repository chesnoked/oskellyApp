//
// Created by Виталий Хлудеев on 25.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CartIconService: UIViewController {

    var countLabels: [UILabel] = []

    func addCartIcon(_ controller: UIViewController) {

        let productCartButton = ProductCartIcon()
        let container = UIView()
        let countLabel = UILabel()
        countLabels.append(countLabel)
        let bubble = UIView()
        let bubbleHeight: CGFloat = 14

        refreshCount()

        container.snp.makeConstraints({m in
            m.width.equalTo(30)
            m.height.equalTo(30)
        })

        container.addSubview(productCartButton)

        productCartButton.controller = controller
        let productCartItem = UIBarButtonItem(customView: container)
        productCartButton.snp.makeConstraints { m in
            m.edges.equalTo(container)
        }
        productCartButton.addTarget(self, action: #selector(self.cartButtonClicked(_:)), for: .touchUpInside)

        bubble.backgroundColor = AppColors.lato()
        bubble.layer.cornerRadius = bubbleHeight / 2
        bubble.layer.borderWidth = 1
        bubble.layer.borderColor = UIColor.black.cgColor
        bubble.layer.zPosition = 10
        container.addSubview(bubble)
        bubble.snp.makeConstraints({m in
            m.right.equalTo(container)
            m.bottom.equalTo(container)
            m.width.equalTo(bubbleHeight)
            m.height.equalTo(bubbleHeight)
        })
        countLabel.font = MediumFont.systemFont(ofSize: 10)
        countLabel.layer.zPosition = 11
        countLabel.textAlignment = .center
        countLabel.text = "0"
        container.addSubview(countLabel)
        countLabel.snp.makeConstraints({m in
            m.center.equalTo(bubble)
        })

        controller.navigationItem.setRightBarButtonItems([productCartItem], animated: true)
    }

    func refreshCount() {
        GlobalProvider.instance.cartProvider.getCart(completionHandler: {c in
            self.countLabels.forEach({
                $0.text = String(c.items?.count ?? 0)
            })
        })
    }

    func cartButtonClicked(_ sender: AnyObject?) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let vc = storyboard.instantiateViewController(withIdentifier: "CartViewController") as! CartViewController
        let button = sender as! ProductCartIcon
        button.controller?.navigationController?.pushViewController(vc, animated: true)
    }
}