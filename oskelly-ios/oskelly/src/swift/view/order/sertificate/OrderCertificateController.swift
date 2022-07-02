//
// Created by Виталий Хлудеев on 23.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderCertificateController : UIViewController {

    private let promoLabel = UILabel()
    private let textField = SimpleRectangleTextField()
    private let saveButton = DarkButton()
    private let continueButton = WhiteButton()

    var order: Order?

    override func viewDidLoad() {
        super.viewDidLoad()
        if(order == nil) {
            order = GlobalProvider.instance.orderProvider.getCurrent()
        }

        navigationItem.title = "Данные о скидке"
        addPromoLabel()
        addTextField()
        addSaveButton()
        addContinueButton()
    }

    private func addContinueButton() {
        view.addSubview(continueButton)
        continueButton.addTarget(self, action: #selector(self.continueButtonClick), for: .touchUpInside)
        continueButton.setTitle("Пропустить", for: .normal)
        continueButton.snp.makeConstraints({m in
            m.top.equalTo(saveButton.snp.bottom).offset(10)
            m.left.equalTo(saveButton)
            m.right.equalTo(saveButton)
            m.height.equalTo(saveButton)
        })
    }

    private func addSaveButton() {
        view.addSubview(saveButton)
        saveButton.addTarget(self, action: #selector(self.saveButtonClick), for: .touchUpInside)
        saveButton.setTitle("Сохранить скидку", for: .normal)
        saveButton.snp.makeConstraints({m in
            m.top.equalTo(textField.snp.bottom).offset(10)
            m.right.equalTo(textField)
            m.left.equalTo(textField)
            m.height.equalTo(50)
        })
    }

    private func addTextField() {
        view.addSubview(textField)
        textField.snp.makeConstraints({m in
            m.centerX.equalTo(view)
            m.width.equalTo(view).multipliedBy(0.9)
            m.height.equalTo(40)
            m.top.equalTo(promoLabel.snp.bottom).offset(10)
        })
    }

    private func addPromoLabel() {
        view.addSubview(promoLabel)
        promoLabel.font = MediumFont.systemFont(ofSize: 14)
        promoLabel.textAlignment = .center
        promoLabel.numberOfLines = 2
        promoLabel.text = "Введите номер сертификата или промокода"
        promoLabel.snp.makeConstraints({ m in
            m.centerX.equalTo(view)
            m.width.equalTo(view).multipliedBy(0.8)
            m.top.equalTo(view).inset(20)
        })
    }

    func continueButtonClick() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        var c = storyboard.instantiateViewController(withIdentifier: "OrderDeliveryController")
        navigationController?.pushViewController(c, animated: true)
    }

    func saveButtonClick() {
        if let id = order?.id {
            saveButton.isEnabled = false
            GlobalProvider.instance.getApiRequester().applyDiscount(orderId: id, code: self.textField.textField.text ?? "", completionHandler: { discount in

                let alert = UIAlertController(title: discount.optionalText.map({"Применен " + $0}), message: discount.discountValue.map({"Вы экономите " + $0 }), preferredStyle: UIAlertControllerStyle.alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: {a in
                    self.continueButtonClick()
                }))
                self.present(alert, animated: true, completion: nil)

                self.order?.appliedDiscount? = discount
                GlobalProvider.instance.orderProvider.setCurrent(order: self.order)
            }, errorHandler: { e in
                self.saveButton.isEnabled = true
                let alert = UIAlertController(title: "Произошла ошибка", message: e, preferredStyle: UIAlertControllerStyle.alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default))
                self.present(alert, animated: true, completion: nil)
            })
        }
    }
}