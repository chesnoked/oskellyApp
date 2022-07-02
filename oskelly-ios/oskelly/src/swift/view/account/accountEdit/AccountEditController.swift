//
// Created by Виталий Хлудеев on 13.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountEditController: UITableViewController {

    var account: Account!

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Персональная информация"
        tableView.register(AccountTextField.self, forCellReuseIdentifier: "Row")
        tableView.register(AccountDateField.self, forCellReuseIdentifier: "DateRow")
        tableView.separatorStyle = .none
        tableView.allowsSelection = false
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
            case 0:
                return 4
            case 1:
                return 5
            case 2:
                return 5
            default:
                return 0
        }
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var seller = account.seller!
        if(seller == nil) {
            seller = Seller()
        }
        switch indexPath.section {
            case 0:
                switch indexPath.row {
                    case 0:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Имя", value: seller.firstName, numbersOnly: false, completeHandler: { a in
                            a.seller?.firstName = cell.getValue()
                        })
                        return cell
                    case 1:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Фамилия", value: seller.lastName, numbersOnly: false, completeHandler: { a in
                            a.seller?.lastName = cell.getValue()
                        })
                        return cell
                    case 2:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "DateRow", for: indexPath) as! AccountDateField
                        cell.render(name: "Дата рождения", value: account.birthDate, numbersOnly: false, completeHandler: { a in
                            a.birthDate = cell.getValue()
                        })
                        return cell
                    case 3:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Телефон", value: seller.phone, numbersOnly: true, completeHandler: { a in
                            a.seller?.phone = cell.getValue()
                        })
                        return cell
                    default:
                        return tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
                }
            case 1:
                var deliveryRequisite = account.deliveryRequisite!
                if(deliveryRequisite == nil) {
                    deliveryRequisite = DeliveryRequisite()
                }
                switch indexPath.row {
                    case 0:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Имя", value: deliveryRequisite.deliveryName, numbersOnly: false, completeHandler: { a in
                            a.deliveryRequisite?.deliveryName = cell.getValue()
                        })
                        return cell
                    case 1:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Телефон", value: deliveryRequisite.deliveryPhone, numbersOnly: true, completeHandler: { a in
                            a.deliveryRequisite?.deliveryPhone = cell.getValue()
                        })
                        return cell
                    /*case 2:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Страна", value: deliveryRequisite.deliveryCountry, numbersOnly: false, completeHandler: { a in
                            a.deliveryRequisite?.deliveryCountry = cell.getValue()
                        })
                        return cell*/
                    case 2:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Город", value: deliveryRequisite.deliveryCity, numbersOnly: false, completeHandler: { a in
                            a.deliveryRequisite?.deliveryCity = cell.getValue()
                        })
                        return cell
                    case 3:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Адрес", value: deliveryRequisite.deliveryAddress, numbersOnly: false, completeHandler: { a in
                            a.deliveryRequisite?.deliveryAddress = cell.getValue()
                        })
                        return cell
                    case 4:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Индекс", value: deliveryRequisite.deliveryZipCode, numbersOnly: true, completeHandler: { a in
                            a.deliveryRequisite?.deliveryZipCode = cell.getValue()
                        })
                        return cell
                    default:
                        return tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
                }
            case 2:
                switch indexPath.row {
                    case 0:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "ФИО или Название компании", value: seller.companyName, numbersOnly: false, completeHandler: { a in
                            a.seller?.companyName = cell.getValue()
                        })
                        return cell
                   /* case 1:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Индекс", value: seller.zipCode, numbersOnly: false, completeHandler: { a in
                            a.seller?.zipCode = cell.getValue()
                        })
                        return cell*/
                    case 1:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Город", value: seller.city, numbersOnly: false, completeHandler: { a in
                            a.seller?.city = cell.getValue()
                        })
                        return cell
                    case 2:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Адрес", value: seller.address, numbersOnly: false, completeHandler: { a in
                            a.seller?.address = cell.getValue()
                        })
                        return cell
                    /*case 4:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Паспорт", value: seller.passport, numbersOnly: false, completeHandler: { a in
                            a.seller?.passport = cell.getValue()
                        })
                        return cell
                    case 5:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "ИНН", value: seller.inn, numbersOnly: false, completeHandler: { a in
                            a.seller?.inn = cell.getValue()
                        })
                        return cell
                    case 6:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "ОГРНИП", value: seller.ogrnip, numbersOnly: false, completeHandler: { a in
                            a.seller?.ogrnip = cell.getValue()
                        })
                        return cell
                    case 7:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "ОГРН", value: seller.ogrn, numbersOnly: false, completeHandler: { a in
                            a.seller?.ogrn = cell.getValue()
                        })
                        return cell
                    case 8:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "КПП", value: seller.kpp, numbersOnly: false, completeHandler: { a in
                            a.seller?.kpp = cell.getValue()
                        })
                        return cell*/
                    case 3:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "БИК", value: seller.bik, numbersOnly: false, completeHandler: { a in
                            a.seller?.bik = cell.getValue()
                        })
                        return cell
                    /*case 10:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Корреспондентский счет", value: seller.correspondentAccount, numbersOnly: false, completeHandler: { a in
                            a.seller?.correspondentAccount = cell.getValue()
                        })
                        return cell*/
                    case 4:
                        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! AccountTextField
                        cell.render(name: "Лицевой/Расчетный счет", value: seller.paymentAccount, numbersOnly: false, completeHandler: { a in
                            a.seller?.paymentAccount = cell.getValue()
                        })
                        return cell
                    default:
                        return tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
                }
            default:
                return tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return BaseTextFieldView.height
    }

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView()
        headerView.backgroundColor = AppColors.transparent()

        let headerLabel = UILabel()
        headerLabel.textAlignment = .center
        headerLabel.font = MediumFont.systemFont(ofSize: 15)
        headerLabel.textColor = .black
        headerLabel.text = self.tableView(self.tableView, titleForHeaderInSection: section)
        headerLabel.sizeToFit()
        headerView.addSubview(headerLabel)
        headerLabel.snp.makeConstraints({ m in
            m.center.equalTo(headerView)
        })

        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        headerView.addSubview(separator)
        separator.snp.makeConstraints({ m in
            m.height.equalTo(1)
            m.bottom.equalTo(headerView)
            m.left.equalTo(headerView)
            m.right.equalTo(headerView)
        })

        return headerView
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 44.0
    }

    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        switch section {
        case 0:
            return "Персональная информация"
        case 1:
            return "Доставка"
        case 2:
            return "Данные продавца"
        default:
            return nil
        }
    }
}