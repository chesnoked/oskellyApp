//
// Created by Виталий Хлудеев on 18.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SaleConfirmRow : UITableViewCell {

    private let confirm = DarkButton()
    private let reject = WhiteButton()
    private let details = WhiteButton()

    var sale: Sale!
    var sellerRequisite: SellerRequisite!
    var controller: SaleController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addConfirm()
        addReject()
        addDetails()
    }

    private func addDetails() {
        contentView.addSubview(details)
        details.setTitle("Детали продажи", for: .normal)
        details.titleLabel?.font = confirm.titleLabel?.font
        details.snp.makeConstraints({ m in
            m.centerX.equalTo(contentView)
            m.top.equalTo(confirm)
            m.height.equalTo(confirm)
            m.width.equalTo(confirm).multipliedBy(1.5)
        })
    }

    private func addReject() {
        contentView.addSubview(reject)
        reject.setTitle("Отклонить", for: .normal)
        reject.titleLabel?.font = confirm.titleLabel?.font
        reject.snp.makeConstraints({ m in
            m.right.equalTo(contentView).inset(10)
            m.top.equalTo(confirm)
            m.height.equalTo(confirm)
            m.width.equalTo(confirm)
        })
        reject.addTarget(self, action: #selector(self.rejectClick), for: .touchUpInside)
    }

    private func addConfirm() {
        contentView.addSubview(confirm)
        confirm.setTitle("Подтвердить", for: .normal)
        confirm.titleLabel?.font = MediumFont.systemFont(ofSize: 12)
        confirm.snp.makeConstraints({ m in
            m.left.equalTo(contentView).inset(10)
            m.top.equalTo(contentView).inset(14)
            m.height.equalTo(35)
            m.width.equalTo(120)
        })
        confirm.addTarget(self, action: #selector(self.confirmClick), for: .touchUpInside)
    }

    func render(sale : Sale, controller: SaleController, sellerRequisite: SellerRequisite) {
        self.sale = sale
        self.sellerRequisite = sellerRequisite
        self.controller = controller
        confirm.isHidden = !sale.needSaleConfirm
        reject.isHidden = !sale.needSaleConfirm
        details.isHidden = sale.needSaleConfirm

        details.isHidden = true
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func confirmClick() {
        let message = "Продажа подтверждена"
        let doConfirmSale = true
        handleClick(doConfirmSale: doConfirmSale, message: message)
    }

    private func handleClick(doConfirmSale: Bool, message: String) {
        confirm.isEnabled = false
        reject.isEnabled = false
        self.controller.navigationItem.rightBarButtonItem = nil
        GlobalProvider.instance.getApiRequester().confirmSale(saleId: sale.id, sellerRequisite: sellerRequisite, doConfirmSale: doConfirmSale, completeHandler: {
            let alert = UIAlertController(title: message, message: nil, preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
            self.controller.present(alert, animated: true, completion: nil)
            self.controller.sale?.needSaleConfirm = false
            self.controller.tableView.reloadData()

            self.reloadSalesController()
            self.reloadSaleGroupsController()

        }, errorHandler: {
            let alert = UIAlertController(title: "Произошла ошибка", message: nil, preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
            self.controller.present(alert, animated: true, completion: nil)
            self.confirm.isEnabled = true
            self.reject.isEnabled = true
            self.controller.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(SaleController.editButtonPressed))
        })
    }

    private func reloadSaleGroupsController() {
        if((self.controller.navigationController?.viewControllers.count ?? 0) >= 3) {
            let i = self.controller.navigationController?.viewControllers.index(of: self.controller)
            let previousController = self.controller.navigationController?.viewControllers[i! - 2] as? SaleGroupsController
            previousController?.loadFromServer()
        }
    }

    private func reloadSalesController() {
        if((self.controller.navigationController?.viewControllers.count ?? 0) >= 2) {
            let i = self.controller.navigationController?.viewControllers.index(of: self.controller)
            let previousController = self.controller.navigationController?.viewControllers[i! - 1] as? SalesController
            if let i = previousController?.saleGroup?.sales?.index(where: { $0.id == self.sale.id }) {
                previousController?.saleGroup?.sales?.remove(at: i)
                previousController?.tableView?.reloadData()
            }
        }
    }

    func rejectClick() {
        let message = "Продажа отклонена"
        let doConfirmSale = false
        handleClick(doConfirmSale: doConfirmSale, message: message)
    }
}