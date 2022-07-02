//
//  AccountOfferRow.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 12.02.18.
//  Copyright © 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountOfferRow : UITableViewCell {

    private let img = UIImageView()
    private let brand = UILabel()
    private let productName = UILabel()
    private let history = UILabel()
    private let stubImg = UIImage(named: "assets/images/300x400.png")
    private let cartButton = WhiteButton()
    private let confirmButton = DarkButton()
    private let declineButton = WhiteButton()

    private var offer : AccountOffer?
    private var controller : AccountOfferListController?

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        isUserInteractionEnabled = true
        addImg()
        addBrand()
        addProductName()
        addHistory()
        addCartButton()
        addConfirmButton()
        addDeclineButton()
        addSeparator()
    }

    private func addSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { m in
            m.height.equalTo(1)
            m.bottom.equalTo(contentView)
            m.right.equalTo(contentView)
            m.left.equalTo(contentView)
        }
    }

    private func addDeclineButton() {
        contentView.addSubview(declineButton)
        declineButton.setTitle("Отклонить", for: .normal)
        declineButton.titleLabel?.font = confirmButton.titleLabel?.font
        declineButton.snp.makeConstraints({m in
            m.top.equalTo(confirmButton)
            m.height.equalTo(confirmButton)
            m.width.equalTo(confirmButton)
            m.left.equalTo(confirmButton.snp.right).offset(7)
        })
        declineButton.addTarget(self, action: #selector(self.declineButtonClick), for: .touchUpInside)
    }

    private func addConfirmButton() {
        contentView.addSubview(confirmButton)
        confirmButton.titleLabel?.font = MediumFont.systemFont(ofSize: 13)
        confirmButton.setTitle("Подтвердить", for: .normal)
        confirmButton.snp.makeConstraints({m in
            m.left.equalTo(history)
            m.height.equalTo(cartButton)
            m.top.equalTo(cartButton)
            m.width.equalTo(history).multipliedBy(0.47)
        })
        confirmButton.addTarget(self, action: #selector(self.confirmButtonClick), for: .touchUpInside)
    }

    private func addCartButton() {
        contentView.addSubview(cartButton)
        cartButton.setTitle("Добавить в корзину", for: .normal)
        cartButton.titleLabel?.font = MediumFont.systemFont(ofSize: 13)
        cartButton.snp.makeConstraints({m in
            m.left.equalTo(history)
            m.top.equalTo(history.snp.bottom).offset(8)
            m.width.equalTo(180)
            m.height.equalTo(28)
        })
        cartButton.addTarget(self, action: #selector(self.cartClick), for: .touchUpInside)
    }

    private func addHistory() {
        contentView.addSubview(history)
        history.numberOfLines = 0
        history.snp.makeConstraints({m in
            m.left.equalTo(brand)
            m.right.equalTo(contentView).inset(5)
            m.top.equalTo(productName.snp.bottom).offset(2)
            m.bottom.equalTo(contentView).inset(60)
        })
    }

    private func addProductName() {
        contentView.addSubview(productName)
        productName.text = "Сапоги"
        productName.numberOfLines = 3
        productName.font = RegularFont.systemFont(ofSize: 14)
        productName.textColor = AppColors.grayLabelText()
        productName.snp.makeConstraints({m in
            m.left.equalTo(brand)
            m.top.equalTo(brand.snp.bottom).offset(2)
        })
    }

    private func addBrand() {
        contentView.addSubview(brand)
        brand.font = BlackFont.systemFont(ofSize: 16)
        brand.numberOfLines = 2
        brand.text = "GUCCI"
        brand.snp.makeConstraints({ m in
            m.top.equalTo(img)
            m.left.equalTo(img.snp.right).offset(10)
            m.right.equalTo(contentView).inset(5)
        })
    }

    private func addImg() {
        img.image = stubImg
        img.contentMode = .scaleAspectFit
        contentView.addSubview(img)
        img.snp.makeConstraints({m in
            m.left.equalTo(contentView).inset(10)
            m.top.equalTo(contentView).inset(10)
            m.width.equalTo(100)
            m.height.equalTo(100 * 4 / 3)
        })
    }

    func render(offer: AccountOffer, controller: AccountOfferListController?) {
        self.offer = offer
        self.controller = controller
        brand.text = offer.brand
        productName.text = (offer.productName ?? "") + (offer.size.map({"\n" + $0}) ?? "")
        img.image = stubImg
        if let im = offer.image {
            let url = URL(string: ApiRequester.domain + im)!
            img.af_setImage(withURL: url, placeholderImage: stubImg)
        }

        let historyString = NSMutableAttributedString()
        var i = 0
        let count = offer.history?.count ?? 0
        offer.history?.forEach({ h in
            let bold = h.bold ?? false
            let text: String = (h.text ?? "") + (h.price.map({(" " + $0 + " ₽").replace(target: " ", withString: "\u{00a0}")}) ?? "")
            historyString.append(
                    Utils.attributedText(
                            withString: text,
                            boldString: bold ? text : "",
                            font: RegularFont.systemFont(ofSize: 14),
                            boldFont: BoldFont.systemFont(ofSize: 14)
                    )
            )
            if(i < count - 1) {
                historyString.append(NSAttributedString(string: "\n"))
            }
            i = i + 1
        })
        history.attributedText = historyString

        cartButton.isHidden = !(offer.canBeAddedToCart ?? false)
        confirmButton.isHidden = !(offer.waitingForConfirmation ?? false)
        declineButton.isHidden = confirmButton.isHidden
    }

    func cartClick() {
        if let id = offer?.productId {
            GlobalProvider.instance.navigator.navigateToProduct(productId: id, controller: controller)
        }
    }

    func confirmButtonClick() {
        confirm(doConfirm: true)
    }

    func declineButtonClick() {
        confirm(doConfirm: false)
    }

    private func confirm(doConfirm: Bool) {
        if let id = offer?.waitingForConfirmationOfferId {
            confirmButton.isEnabled = false
            declineButton.isEnabled = false
            GlobalProvider.instance.getApiRequester().confirmAccountOffer(offerId: id, doConfirm: doConfirm, completeHandler: {o in
                self.controller?.loadOffers()
            }, errorHandler: {e in
                self.confirmButton.isEnabled = true
                self.declineButton.isEnabled = true
                let alert = UIAlertController(title: "Ошибка", message: e, preferredStyle: UIAlertControllerStyle.alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
                self.controller?.present(alert, animated: true, completion: nil)
            })
        }
        else {
            let alert = UIAlertController(title: "Ошибка", message: "Предложение не нуждается в подтверждении", preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
            self.controller?.present(alert, animated: true, completion: nil)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
