//
// Created by Виталий Хлудеев on 31.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderDeliveryController: OrderBaseController {

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(OrderDeliveryRow.self, forCellReuseIdentifier: "Row")
        navigationItem.title = "Подтверждение адреса"
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }

    public override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath) as! OrderDeliveryRow

        let deliveryRequisite: DeliveryRequisite = order?.deliveryRequisite ?? DeliveryRequisite()

        switch indexPath.row {
        case 0:
            cell.render(name: "Имя", value: deliveryRequisite.deliveryName, numbersOnly: false, completionHandler: { r in
                r.deliveryName = cell.getValue()
            })
        case 1:
            cell.render(name: "Телефон", value: deliveryRequisite.deliveryPhone, numbersOnly: true, completionHandler: { r in
                r.deliveryPhone = cell.getValue()
            })
        case 2:
            cell.render(name: "Город", value: deliveryRequisite.deliveryCity, numbersOnly: false, completionHandler: { r in
                r.deliveryCity = cell.getValue()
            })
        case 3:
            cell.render(name: "Адрес", value: deliveryRequisite.deliveryAddress, numbersOnly: false, completionHandler: { r in
                r.deliveryAddress = cell.getValue()
                r.deliveryCountry = "Россия"
            })
        case 4:
            cell.render(name: "Почтовый индекс", value: deliveryRequisite.deliveryZipCode, numbersOnly: true, completionHandler: { r in
                r.deliveryZipCode = cell.getValue()
            })
        default:
            return tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath)
        }
        return cell
    }

    public override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 5
    }

    override func renderFooter() {
        footer.render(completionHandler: {
            if let id = self.order?.id {
                GlobalProvider.instance.getApiRequester().initHold(orderId: id, completionHandler: { r in
                    var paymentRequest = PaymentRequest()
                    paymentRequest.request = r

                    GlobalProvider.instance.paymentRequestProvider.setCurrent(paymentRequest: paymentRequest)

                    let storyboard = UIStoryboard(name: "Main", bundle: nil)
                    var c = storyboard.instantiateViewController(withIdentifier: "OrderSummaryController")
                    self.navigationController?.pushViewController(c, animated: true)
                })
            }
        }, buttonText: "Подтвердить адрес")
    }
}