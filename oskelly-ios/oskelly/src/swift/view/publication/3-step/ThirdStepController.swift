//
// Created by Виталий Хлудеев on 25.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ThirdStepController : BaseStepController, UITableViewDataSource, UITableViewDelegate {

    let footer = UIView()
    let footerHeight: CGFloat = 100.0
    let sellButton = DarkButton()

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Описание"
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
        sellButton.addTarget(self, action: #selector(ThirdStepController.goToFourthStepController(_:)), for: .touchUpInside)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
        switch indexPath.row{
            case 0:
                cell.render(attributeName: "Описание", selectedValue: nil)
            case 1:
                cell.render(attributeName: "Дополнительно", selectedValue: nil)
            default:
                return cell
        }
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if(indexPath.row == 0) {
            let c = storyboard.instantiateViewController(withIdentifier: "PublicationDescriptionController")
            navigationController?.pushViewController(c, animated: true)
        }
        if(indexPath.row == 1) {
            let c = storyboard.instantiateViewController(withIdentifier: "PublicationAdditionalController")
            navigationController?.pushViewController(c, animated: true)
        }
    }

    func goToFourthStepController(_ sender: Any){
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "PublicationStepsController") as! PublicationStepsController
        navigationController?.pushViewController(c, animated: true)

    }
}