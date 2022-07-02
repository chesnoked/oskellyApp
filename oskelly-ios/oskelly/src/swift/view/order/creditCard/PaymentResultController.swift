//
// Created by Виталий Хлудеев on 06.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PaymentResultController : OrderBaseController {

    var paymentStatus : String!

    let header = UIView()
    let headerTitle = UILabel()

    let headerHeight : CGFloat = 60.0

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(OrderRow.self, forCellReuseIdentifier: "Row")
        navigationItem.title = "Результат оплаты"

        header.addSubview(headerTitle)
        headerTitle.snp.makeConstraints({m in
            m.center.equalTo(header)
        })
        headerTitle.font = MediumFont.systemFont(ofSize: 17)
        headerTitle.textColor = .white
        if(paymentStatus == PaymentStatus.STATUS_HOLD_WAIT) {
            header.backgroundColor = UIColor(displayP3Red: 14/256, green: 190/256, blue: 128/256, alpha: 1)
            headerTitle.text = "Оплата прошла успешно"
        }
        else {
            header.backgroundColor = UIColor(displayP3Red: 215/256, green: 84/256, blue: 83/256, alpha: 1)
            headerTitle.text = "Оплатить заказ не удалось"
        }
    }

    public override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return order?.items?.count ?? 0
    }

    public override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! OrderRow
        if let item = order?.items?[indexPath.row] {
            cell.render(orderPosition: item)
        }
        return cell
    }

    override func renderFooter() {
        footer.render(completionHandler: {
            self.navigationController?.popToRootViewController(animated: true)
        }, buttonText: "OK")
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 105
    }

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return header
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return headerHeight
    }
}