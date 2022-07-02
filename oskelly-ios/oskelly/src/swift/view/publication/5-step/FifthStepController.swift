//
// Created by Виталий Хлудеев on 27.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class FifthStepController : UITableViewController {

    var draft: Draft!
    let footer = PublicationStepBaseFooter()

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Состояние и цена"
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        tableView.register(PublicationVintageView.self, forCellReuseIdentifier: "PublicationVintageView")
        tableView.register(PublicationTextFieldView.self, forCellReuseIdentifier: "PublicationTextFieldView")
        tableView.separatorStyle = .none
        tableView.allowsSelection = false

        footer.controller = self
        footer.nextStepHandler = self.goToBaseStepController(_:)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 6
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if draft.sellerRequisite == nil {
            draft.sellerRequisite = SellerRequisite()
        }
        switch indexPath.row {
        case 0:
            let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
            cell.render(name: "Номер телефона", value: draft.sellerRequisite!.phone, numbersOnly: true) { d in
                d.sellerRequisite!.phone = cell.getValue()
            }
            return cell
        case 1:
            let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
            cell.render(name: "Имя", value: draft.sellerRequisite!.firstName, numbersOnly: false) { d in
                d.sellerRequisite!.firstName = cell.getValue()
            }
            return cell
        case 2:
            let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
            cell.render(name: "Фамилия", value: draft.sellerRequisite!.lastName, numbersOnly: false) { d in
                d.sellerRequisite!.lastName = cell.getValue()
            }
            return cell
        case 3:
            let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
            cell.render(name: "Почтовый индекс", value: draft.sellerRequisite!.zipCode, numbersOnly: true) { d in
                d.sellerRequisite!.zipCode = cell.getValue()
            }
            return cell
        case 4:
            let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
            cell.render(name: "Город", value: draft.sellerRequisite!.city, numbersOnly: false) { d in
                d.sellerRequisite!.city = cell.getValue()
            }
            return cell
        case 5:
            let cell = tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath) as! PublicationTextFieldView
            cell.render(name: "Адрес", value: draft.sellerRequisite!.address, numbersOnly: false) { d in
                d.sellerRequisite!.address = cell.getValue()
            }
            return cell
        default:
            return tableView.dequeueReusableCell(withIdentifier: "PublicationTextFieldView", for: indexPath)
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return BaseTextFieldView.height
    }

    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return footer
    }
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return PublicationStepBaseFooter.height
    }

    func goToBaseStepController(_ sender: Any){
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "PublicationStepsController") as! PublicationStepsController
        navigationController?.pushViewController(c, animated: true)

    }

}