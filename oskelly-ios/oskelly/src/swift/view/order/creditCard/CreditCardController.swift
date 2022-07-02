//
// Created by Виталий Хлудеев on 01.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CreditCardController : OrderBaseController {

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(CreditCardRow.self, forCellReuseIdentifier: "Row")
        navigationItem.title = "Оплата"
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }

    public override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! CreditCardRow

        switch indexPath.row {
        case 0:
            cell.render(name: "Номер карты", numbersOnly: true, placeholder: "Номер карты", maxLength: 20, completionHandler: { c in
                c.number = cell.getValue()
            })
        case 1:
            cell.render(name: "Срок действия - месяц", numbersOnly: true, placeholder: "ММ", maxLength: 2, completionHandler: { c in
                if(cell.getValue() != nil && cell.getValue()! != "") {
                    c.expiryDate!.month = Int(cell.getValue()!)
                }
            })
        case 2:
            cell.render(name: "Срок действия - год", numbersOnly: true, placeholder: "ГГГГ", maxLength: 4, completionHandler: { c in
                if(cell.getValue() != nil && cell.getValue()! != "") {
                    c.expiryDate!.year = Int(cell.getValue()!)
                }
            })
        case 3:
            cell.render(name: "CVC код безопасности", numbersOnly: true, placeholder: "CVC код", maxLength: 4, completionHandler: { c in
                c.cvc2 = cell.getValue()
            })
            cell.textField.isSecureTextEntry = true
        default:
            return tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath)
        }
        return cell

    }

    public override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }

    override func renderFooter() {
        footer.render(completionHandler: {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let c = storyboard.instantiateViewController(withIdentifier: "D3SecureController")
            self.navigationController?.pushViewController(c, animated: true)
        }, buttonText: "Оплатить")
    }
}