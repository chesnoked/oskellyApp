//
// Created by Виталий Хлудеев on 27.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit


class PublicationPriceController : UIViewController, UITextFieldDelegate {

    private let promoLabel = UILabel()
    private let textField = SimpleRectangleTextField()
    private let yourPriceLabel = UILabel()

    var draft: Draft?

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Цена"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "OK", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        addPromoLabel()
        addTextField()
        addYourPriceLabel()
    }

    private func addYourPriceLabel() {
        view.addSubview(yourPriceLabel)
        yourPriceLabel.textAlignment = .center
        yourPriceLabel.font = MediumFont.systemFont(ofSize: 14)
        yourPriceLabel.textColor = .gray
        yourPriceLabel.numberOfLines = 2
        yourPriceLabel.snp.makeConstraints({m in
            m.centerX.equalTo(view)
            m.top.equalTo(textField.snp.bottom).offset(10)
        })
        yourPriceLabel.text = draft?.priceWithoutCommission.map({"Вы получите:\n" +  String($0) + " ₽"})
    }

    private func addPromoLabel() {
        view.addSubview(promoLabel)
        promoLabel.font = MediumFont.systemFont(ofSize: 14)
        promoLabel.textAlignment = .center
        promoLabel.numberOfLines = 2
        promoLabel.text = "Цена на сайте"
        promoLabel.snp.makeConstraints({ m in
            m.centerX.equalTo(view)
            m.width.equalTo(view).multipliedBy(0.8)
            m.top.equalTo(view).inset(20)
        })
    }

    private func addTextField() {
        view.addSubview(textField)
        textField.textField.delegate = self
        textField.textField.keyboardType = .numberPad
        textField.textField.text = draft?.priceWithCommission.map({String($0)})
        textField.snp.makeConstraints({m in
            m.centerX.equalTo(view)
            m.width.equalTo(view).multipliedBy(0.9)
            m.height.equalTo(40)
            m.top.equalTo(promoLabel.snp.bottom).offset(10)
        })
        textField.textField.addTarget(self, action: #selector(self.onChange), for: .editingChanged)
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        draft?.priceWithCommission = ((textField.text ?? "0") as NSString).floatValue
        if let d = draft {
            GlobalProvider.instance.draftProvider.setCurrent(draft: d)
            GlobalProvider.instance.draftProvider.publish(draft: d) { d in
            }
        }
    }

    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let allowedCharacters = CharacterSet.decimalDigits
        let characterSet = CharacterSet(charactersIn: string)
        return allowedCharacters.isSuperset(of: characterSet)
    }

    func onChange() {
        self.yourPriceLabel.text = "Вы получите:\n..."
        if let id = draft?.id {
            GlobalProvider.instance.getApiRequester().getPriceWithoutCommission(productId: id, price: textField.textField.text ?? "", completeHandler: { price in
                self.yourPriceLabel.text = "Вы получите:\n" +  String(price) + " ₽"
            })
        }
    }

    func okPressed() {
        navigationController?.popViewController(animated: true)
    }
}