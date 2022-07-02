//
// Created by Виталий Хлудеев on 18.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SaleListRow: SaleBaseRow {

    private let confirm = WhiteButton()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addConfirm()
    }

    private func addConfirm() {
        contentView.addSubview(confirm)
        confirm.setTitle("Продолжить", for: .normal)
        confirm.titleLabel?.font = MediumFont.systemFont(ofSize: 12)
        confirm.snp.makeConstraints({m in
            m.left.equalTo(productId)
            makeTopOffset(m, buyPriceWithoutCommission)
            m.height.equalTo(30)
            m.right.equalTo(contentView).inset(25)
        })
        confirm.addTarget(self, action: #selector(self.confirmClick), for: .touchUpInside)
    }

    override func render(sale : Sale, controller: UITableViewController) {
        super.render(sale: sale, controller: controller)
        confirm.setTitle(sale.needSaleConfirm ? "Подтверждение" : "Продолжить", for: .normal)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func confirmClick() {
        controller.tableView.isUserInteractionEnabled = false
        let c = self.controller.storyboard!.instantiateViewController(withIdentifier: "SaleController") as! SaleController
        c.sale = self.sale
        self.controller.navigationController?.pushViewController(c, animated: true)
        self.controller.tableView.isUserInteractionEnabled = true
    }
}