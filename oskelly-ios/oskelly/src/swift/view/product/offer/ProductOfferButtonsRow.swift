//
// Created by Виталий Хлудеев on 10.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductOfferButtonsRow : UITableViewCell {

    private let textFieldContainer = SimpleRectangleTextField()
    private let textField = UITextField()
    private let offerButton = DarkButton()
    private let offerSuccessButton = DarkButton()
    private let message = UILabel()

    private var product: Product!
    private var controller: ProductOfferController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        addMessage()
        addTextField()
        addOfferButton()
        addOfferSuccessButton()
    }

    private func addOfferSuccessButton() {
        contentView.addSubview(offerSuccessButton)
        offerSuccessButton.setTitle("Перейти к товару для оформления заказа", for: .normal)
        offerSuccessButton.snp.makeConstraints({m in
            m.width.equalTo(textFieldContainer)
            m.height.equalTo(45)
            m.centerX.equalTo(textFieldContainer)
            m.top.equalTo(textFieldContainer.snp.bottom).offset(7)
            m.bottom.equalTo(contentView).inset(20)
        })

        offerSuccessButton.addTarget(self, action: #selector(self.offerSuccessButtonClick), for: .touchUpInside)
    }

    private func addOfferButton() {
        contentView.addSubview(offerButton)
        offerButton.setTitle("Предложить цену", for: .normal)
        offerButton.snp.makeConstraints({m in
            m.width.equalTo(textFieldContainer)
            m.height.equalTo(45)
            m.centerX.equalTo(textFieldContainer)
            m.top.equalTo(textFieldContainer.snp.bottom).offset(7)
            m.bottom.equalTo(contentView).inset(20)
        })

        offerButton.addTarget(self, action: #selector(self.makeOffer), for: .touchUpInside)
    }

    private func addTextField() {
        textFieldContainer.textField.keyboardType = .numberPad
        textFieldContainer.setPlaceHolder("Цена")
        contentView.addSubview(textFieldContainer)
        textFieldContainer.snp.makeConstraints({ m in
            m.centerX.equalTo(contentView)
            m.width.equalTo(contentView).multipliedBy(0.9)
            m.height.equalTo(40)
            m.top.equalTo(message.snp.bottom).offset(10)
        })
    }

    private func addMessage() {
        contentView.addSubview(message)
        message.text = "Ваше предложение:"
        message.numberOfLines = 0
        message.font = MediumFont.systemFont(ofSize: 14)
        message.textAlignment = .center
        message.snp.makeConstraints({ m in
            m.centerX.equalTo(contentView)
            m.top.equalTo(contentView).inset(5)
            m.width.equalTo(contentView).multipliedBy(0.8)
        })
    }

    func render(product: Product, controller: ProductOfferController) {
        self.product = product
        self.controller = controller
        let allowsNegotiation: Bool = product.offerRelated?.allowsNegotiation ?? false
        message.text = allowsNegotiation ? "Ваше предложение:" : product.offerRelated?.reasonIfNotNegotiable
        textFieldContainer.isHidden = !allowsNegotiation
        offerButton.isHidden = !allowsNegotiation

        textFieldContainer.snp.remakeConstraints({ m in
            m.centerX.equalTo(contentView)
            m.width.equalTo(contentView).multipliedBy(0.9)
            m.height.equalTo(allowsNegotiation ? 40 : 0)
            m.top.equalTo(message.snp.bottom).offset(allowsNegotiation ? 10 : 0)
        })

        offerButton.isHidden = !allowsNegotiation
        offerButton.snp.remakeConstraints({m in
            m.width.equalTo(textFieldContainer)
            m.height.equalTo(allowsNegotiation ? 45 : 0)
            m.centerX.equalTo(textFieldContainer)
            m.top.equalTo(textFieldContainer.snp.bottom).offset(allowsNegotiation ? 7 : 0)
            if(product.offerRelated?.negotiatedPrice == nil) { // иначе конфликт с offerSuccessButton
                m.bottom.equalTo(contentView).inset(20)
            }
        })

        offerSuccessButton.isHidden = product.offerRelated?.negotiatedPrice == nil
        offerSuccessButton.snp.remakeConstraints({m in
            m.width.equalTo(textFieldContainer)
            m.height.equalTo(product.offerRelated?.negotiatedPrice != nil ? 45 : 0)
            m.centerX.equalTo(textFieldContainer)
            m.top.equalTo(textFieldContainer.snp.bottom).offset(product.offerRelated?.negotiatedPrice != nil ? 7 : 0)
            if(product.offerRelated?.negotiatedPrice != nil) { // иначе конфликт с offerButton
                m.bottom.equalTo(contentView).inset(20)
            }
        })
    }

    func makeOffer() {
        offerButton.isEnabled = false
        offerButton.setTitle("Предложение публикуется", for: .normal)
        GlobalProvider.instance.getApiRequester().makeOffer(productId: product.id, price: textFieldContainer.textField.text ?? "", completeHandler: { o in
            self.controller.product.offerRelated = o
            self.controller.tableView.reloadData()
            self.controller.parentController?.product?.offerRelated = o
        }, errorHandler: { e in
            let alert = UIAlertController(title: "Не удалось сделать предложение", message: e, preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
            self.controller.present(alert, animated: true, completion: nil)

            self.offerButton.isEnabled = true
            self.offerButton.setTitle("Предложить цену", for: .normal)
        })
    }

    func offerSuccessButtonClick() {
        if let c = controller.parentController {
            controller.navigationController?.popToViewController(c, animated: true)
        }
        else {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let c = storyboard.instantiateViewController(withIdentifier: "ProductViewController") as! ProductViewController
            c.productId = product.id
            controller.navigationController?.pushViewController(c, animated: true)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}