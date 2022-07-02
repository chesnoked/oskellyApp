//
// Created by Виталий Хлудеев on 27.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class FourthStepController : BaseStepController, UITableViewDataSource, UITableViewDelegate {

    var conditions: [ProductCondition] = []

    let footer = UIView()
    let footerHeight: CGFloat = 100.0
    let sellButton = DarkButton()

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Продавец"
        tableView.tableFooterView = UIView()
        tableView.dataSource = self
        tableView.delegate = self
        tableView.snp.makeConstraints { make -> Void in
            make.edges.equalTo(view).inset(UIEdgeInsetsMake(0, 0, footerHeight, 0))
        }

        view.addSubview(footer)
        footer.backgroundColor = AppColors.transparent()
        footer.snp.makeConstraints { make in
            make.top.equalTo(tableView.snp.bottom)
            make.left.equalTo(view)
            make.right.equalTo(view)
            make.bottom.equalTo(view)
        }
        footer.addSubview(sellButton)
        sellButton.setTitle("Продолжить", for: .normal)
        sellButton.snp.makeConstraints { make -> Void in
            make.center.equalTo(footer)
            make.width.equalTo(footer).multipliedBy(0.9)
        }
        sellButton.addTarget(self, action: #selector(FourthStepController.goToFifthStepController(_:)), for: .touchUpInside)

        GlobalProvider.instance.directoriesProvider.getConditions { conditions in
            self.conditions = conditions
            self.tableView.reloadData()
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        tableView.reloadData()
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
        switch indexPath.row{
            case 0:
                let condition = conditions.filter({$0.id == draft.selectedCondition}).first
                cell.render(attributeName: "Состояние", selectedValue: condition?.name)
            case 1:
                cell.render(attributeName: "Цена", selectedValue: draft.priceWithCommission != nil ? String(draft.priceWithCommission!) : "")
            default:
                return cell
        }
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {

        tableView.deselectRow(at: indexPath, animated: false)

        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        var c : UIViewController!
        switch indexPath.row {
            case 0:
                c = storyboard.instantiateViewController(withIdentifier: "PublicationConditionController") as! PublicationConditionController
            case 1:
                c = storyboard.instantiateViewController(withIdentifier: "PublicationPriceController") as! PublicationPriceController
            default:
                return
        }
        navigationController?.pushViewController(c, animated: true)
    }

    func goToFifthStepController(_ sender: Any){
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "PublicationStepsController") as! PublicationStepsController
        navigationController?.pushViewController(c, animated: true)

    }
}