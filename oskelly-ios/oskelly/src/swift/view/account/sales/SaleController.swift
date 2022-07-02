//
// Created by Виталий Хлудеев on 18.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SaleController : UITableViewController {

    var sale : Sale?
    var productItemId: Int?
    var sellerRequisite : SellerRequisite = SellerRequisite()
    var loaded = false
    var isEditMode = false

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.separatorStyle = .none
        tableView.register(SaleSingleRow.self, forCellReuseIdentifier: "SaleRow")
        tableView.register(SubmitAttributeRow.self, forCellReuseIdentifier: "AddressRow")
        tableView.register(SaleSubmitAddressRow.self, forCellReuseIdentifier: "SaleSubmitAddressRow")
        tableView.register(SaleConfirmRow.self, forCellReuseIdentifier: "SaleConfirmRow")
        tableView.allowsSelection = false
        self.getSale(completionHandler: { s in
            self.navigationItem.title = s.brandName
            GlobalProvider.instance.getApiRequester().getSaleRequisite(saleId: s.id, completionHandler: { sellerRequisite in
                self.sellerRequisite = sellerRequisite
                self.loaded = true
                self.tableView.reloadData()
            })
            if(s.needSaleConfirm) {
                self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.editButtonPressed))
            }
            else {
                self.navigationItem.rightBarButtonItem = nil
            }
            self.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Назад", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.cancelButtonPressed))
        })
    }

    func getSale(completionHandler: @escaping (Sale) -> ()) {
        if let s = sale {
            completionHandler(s)
            return
        }
        if let id = productItemId {
            GlobalProvider.instance.getApiRequester().getSale(productItemId: id, completionHandler: { s in
                self.sale = s
                self.tableView.reloadData()
                completionHandler(s)
                return
            }, errorHandler: {e in
                let alert = UIAlertController(title: "Ошибка", message: e, preferredStyle: UIAlertControllerStyle.alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
                self.present(alert, animated: true, completion: nil)
            })
        }
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(!loaded) {
            return 0
        }
        switch section {
            case 0:
                return 1
            case 1:
                return 6
            case 2:
                return 1
            default:
                assert(false)
        }
        return 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let s = sale {
            switch indexPath.section {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "SaleRow", for: indexPath as IndexPath) as! SaleSingleRow
                cell.render(sale: s, controller: self)
                return cell
            case 1:
                switch indexPath.row {
                case 0:
                    if (!isEditMode) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "AddressRow", for: indexPath as IndexPath) as! SubmitAttributeRow
                        cell.render(attributeName: "Номер телефона", selectedValue: sellerRequisite.phone, true)
                        return cell
                    } else {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "SaleSubmitAddressRow", for: indexPath as IndexPath) as! SaleSubmitAddressRow
                        cell.render(name: "Номер телефона", value: sellerRequisite.phone, keyboardType: .phonePad, enable: true)
                        return cell
                    }
                case 1:
                    if (!isEditMode) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "AddressRow", for: indexPath as IndexPath) as! SubmitAttributeRow
                        cell.render(attributeName: "Имя", selectedValue: sellerRequisite.firstName, true)
                        return cell
                    } else {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "SaleSubmitAddressRow", for: indexPath as IndexPath) as! SaleSubmitAddressRow
                        cell.render(name: "Имя", value: sellerRequisite.firstName, keyboardType: .default, enable: true)
                        return cell
                    }
                case 2:
                    if (!isEditMode) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "AddressRow", for: indexPath as IndexPath) as! SubmitAttributeRow
                        cell.render(attributeName: "Фамилия", selectedValue: sellerRequisite.lastName, true)
                        return cell
                    } else {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "SaleSubmitAddressRow", for: indexPath as IndexPath) as! SaleSubmitAddressRow
                        cell.render(name: "Фамилия", value: sellerRequisite.lastName, keyboardType: .default, enable: true)
                        return cell
                    }
                case 3:
                    if (!isEditMode) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "AddressRow", for: indexPath as IndexPath) as! SubmitAttributeRow
                        cell.render(attributeName: "Почтовый адрес", selectedValue: sellerRequisite.zipCode, true)
                        return cell
                    } else {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "SaleSubmitAddressRow", for: indexPath as IndexPath) as! SaleSubmitAddressRow
                        cell.render(name: "Почтовый адрес", value: sellerRequisite.zipCode, keyboardType: .default, enable: false)
                        return cell
                    }
                case 4:
                    if (!isEditMode) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "AddressRow", for: indexPath as IndexPath) as! SubmitAttributeRow
                        cell.render(attributeName: "Город", selectedValue: sellerRequisite.city, true)
                        return cell
                    } else {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "SaleSubmitAddressRow", for: indexPath as IndexPath) as! SaleSubmitAddressRow
                        cell.render(name: "Город", value: sellerRequisite.city, keyboardType: .default, enable: false)
                        return cell
                    }
                case 5:
                    if (!isEditMode) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "AddressRow", for: indexPath as IndexPath) as! SubmitAttributeRow
                        cell.render(attributeName: "Адрес", selectedValue: sellerRequisite.address, true)
                        return cell
                    } else {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "SaleSubmitAddressRow", for: indexPath as IndexPath) as! SaleSubmitAddressRow
                        cell.render(name: "Адрес", value: sellerRequisite.address, keyboardType: .default, enable: false)
                        return cell
                    }
                default:
                    assert(false)
                }
            case 2:
                let cell = tableView.dequeueReusableCell(withIdentifier: "SaleConfirmRow", for: indexPath as IndexPath) as! SaleConfirmRow
                cell.render(sale: s, controller: self, sellerRequisite: sellerRequisite)
                return cell
            default:
                assert(false)
            }
        }
        return tableView.dequeueReusableCell(withIdentifier: "", for: indexPath as IndexPath)
    }

    public override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch indexPath.section {
            case 0:
                var height : CGFloat = 215
                if(sale?.size == nil) {
                    height = height - 22
                }
                return height
            case 2:
                return 60.0
            default:
                return 44.0
        }
    }

    func editButtonPressed() {
        if(!isEditMode) {
            isEditMode = true
            (tableView as! UITableView).reloadRows(at: [
                IndexPath(row: 0, section: 1),
                IndexPath(row: 1, section: 1),
                IndexPath(row: 2, section: 1),
                IndexPath(row: 3, section: 1),
                IndexPath(row: 4, section: 1),
                IndexPath(row: 5, section: 1)
            ], with: .left)
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
            navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Отмена", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.cancelButtonPressed))
        }
        else {
            isEditMode = false

            self.sellerRequisite.phone = (tableView.cellForRow(at: IndexPath(row: 0, section: 1)) as! SaleSubmitAddressRow).getValue()
            self.sellerRequisite.firstName = (tableView.cellForRow(at: IndexPath(row: 1, section: 1)) as! SaleSubmitAddressRow).getValue()
            self.sellerRequisite.lastName = (tableView.cellForRow(at: IndexPath(row: 2, section: 1)) as! SaleSubmitAddressRow).getValue()

            (tableView as! UITableView).reloadRows(at: [
                IndexPath(row: 0, section: 1),
                IndexPath(row: 1, section: 1),
                IndexPath(row: 2, section: 1),
                IndexPath(row: 3, section: 1),
                IndexPath(row: 4, section: 1),
                IndexPath(row: 5, section: 1)
            ], with: .right)
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
            navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Назад", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.cancelButtonPressed))
        }
    }

    func cancelButtonPressed() {
        if isEditMode == true {
//            cart = Cart(object: cartBackup.dictionaryRepresentation())
//            itemsToDelete.removeAll()
            self.isEditMode = false
            (tableView as! UITableView).reloadRows(at: [
                IndexPath(row: 0, section: 1),
                IndexPath(row: 1, section: 1),
                IndexPath(row: 2, section: 1),
                IndexPath(row: 3, section: 1),
                IndexPath(row: 4, section: 1),
                IndexPath(row: 5, section: 1)
            ], with: .right)
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
            navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Назад", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.cancelButtonPressed))
        }
        else {
            self.navigationController?.popViewController(animated: true)
        }
    }
}