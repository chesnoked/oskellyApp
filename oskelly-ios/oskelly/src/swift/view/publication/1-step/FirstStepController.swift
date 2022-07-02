//
// Created by Виталий Хлудеев on 18.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class FirstStepController : BaseStepController, UITableViewDataSource, UITableViewDelegate {

    var attributes: [Attribute] = []

    var sizes: [Size] = []

    let footer = UIView()

    let footerHeight: CGFloat = 100.0

    let sellButton = DarkButton()

    override func viewDidLoad() {
        super.viewDidLoad()
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
        sellButton.addTarget(self, action: #selector(FirstStepController.goToSecondStepController(_:)), for: .touchUpInside)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        GlobalProvider.instance.getApiRequester().getAttributes(categoryId: self.draft.category!) { attributes in
            GlobalProvider.instance.getApiRequester().getSizes(categoryId: self.draft.category!) { sizes in
                self.sizes = sizes
                self.attributes = attributes
                self.tableView.reloadData()
            }
        }
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return attributes.count + 1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if(indexPath.row == attributes.count) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
            cell.render(attributeName: "Размер", selectedValue: (draft.selectedSizeType != nil &&  draft.selectedSize?.value != nil) ? draft.selectedSizeType! + " " + draft.selectedSize!.value! : "")
            return cell
        }
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
        var attributeValue : String?;
        if let i: Int =  draft.selectedAttributeValues!.index(where: { $0.attributeId == attributes[indexPath.row].id }) {
            attributeValue = draft.selectedAttributeValues![i].value
        }
        cell.render(attributeName: attributes[indexPath.row].name, selectedValue: attributeValue)
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        if (indexPath.row == attributes.count) {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let c = storyboard.instantiateViewController(withIdentifier: "PublicationSubmitSizeController") as! PublicationSubmitSizeController
            c.sizes = sizes
            navigationController?.pushViewController(c, animated: true)
            return
        }
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "PublicationSubmitAttributeController") as! PublicationSubmitAttributeController
        c.draft = draft
        c.attribute = attributes[indexPath.row]
        navigationController?.pushViewController(c, animated: true)
    }

    func goToSecondStepController(_ sender: Any){
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "PublicationStepsController") as! PublicationStepsController
        navigationController?.pushViewController(c, animated: true)

    }
}