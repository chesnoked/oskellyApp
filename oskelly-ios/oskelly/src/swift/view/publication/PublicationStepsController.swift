//
// Created by Виталий Хлудеев on 17.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationStepsController : UIViewController, UITableViewDataSource, UITableViewDelegate {

    let tableView = UITableView()

    var parentCategory: ProductCategory? = nil

    var dataSource: [String] = ["Информация", "Фото","Описание","Состояние и цена","Продавец"]

    var draft: Draft!

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
        tableView.register(PublicationStepSummaryRow.self, forCellReuseIdentifier: "CatalogRow")
        tableView.register(SubmitCategoryHeader.self, forCellReuseIdentifier: "CatalogRowHeader")
        tableView.separatorStyle = .none
        tableView.backgroundColor = AppColors.transparent()
        self.view.addSubview(tableView)
        tableView.snp.makeConstraints { make -> Void in
            make.edges.equalTo(view).inset(UIEdgeInsetsMake(0, 0, footerHeight, 0))
        }
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        GlobalProvider.instance.getApiRequester().getAttributes(categoryId: self.draft.category!) { attributes in
            GlobalProvider.instance.getApiRequester().getSizes(categoryId: self.draft.category!) { sizes in
                self.sizes = sizes
                self.attributes = attributes
                self.tableView.reloadData()
            }
        }
        navigationItem.title = draft.categoryName

        view.addSubview(footer)
        footer.backgroundColor = AppColors.transparent()
        footer.snp.makeConstraints { make in
            make.top.equalTo(tableView.snp.bottom)
            make.left.equalTo(view)
            make.right.equalTo(view)
            make.bottom.equalTo(view)
        }
        footer.addSubview(sellButton)
        sellButton.setTitle("Подтвердить", for: .normal)
        sellButton.snp.makeConstraints { make -> Void in
            make.center.equalTo(footer)
            make.width.equalTo(footer).multipliedBy(0.9)
        }
        sellButton.addTarget(self, action: #selector(PublicationStepsController.selButtonTapped(_:)), for: .touchUpInside)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        self.tableView.reloadData()
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(5), execute: {
            self.draft = GlobalProvider.instance.draftProvider.getCurrent()
            self.tableView.reloadData()
        })
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.count + 1 // Добавляем хэдэр
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.row != 0) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CatalogRow", for: indexPath as IndexPath) as! PublicationStepSummaryRow
            cell.render(stepNumber: indexPath.row, stepName: dataSource[indexPath.row - 1], completed: draft.completedSteps.index(where: {$0 == indexPath.row}) != nil)
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CatalogRowHeader", for: indexPath as IndexPath) as! SubmitCategoryHeader
            cell.catalogNameLabel.text = draft.brandName! + "\n" + draft.categoryName!
            return cell
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        let storyboard = UIStoryboard(name: "Main", bundle: nil)

        var c : UIViewController!
        switch indexPath.row {
            case 0:
                return
            case 1:
                c = storyboard.instantiateViewController(withIdentifier: "FirstStepController") as! FirstStepController
                (c as! FirstStepController).sizes = sizes
                (c as! FirstStepController).attributes = attributes
            case 2:
                c = storyboard.instantiateViewController(withIdentifier: "SecondStepController") as! SecondStepController
            case 3:
                c = storyboard.instantiateViewController(withIdentifier: "ThirdStepController") as! ThirdStepController
            case 4:
                c = storyboard.instantiateViewController(withIdentifier: "FourthStepController") as! FourthStepController
            case 5:
                c = storyboard.instantiateViewController(withIdentifier: "FifthStepController") as! FifthStepController
            default:
                return
        }
        navigationController?.pushViewController(c, animated: true)
        c.navigationItem.title = dataSource[indexPath.row - 1]
    }

    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if(indexPath.row == 0) {
            return 132.0
        }
        return 64.0
    }

    func selButtonTapped(_ sender: Any) {
        GlobalProvider.instance.draftProvider.publish(draft: draft, completionHandler: { draft, error in
            if error == nil {
                let alert = UIAlertController(title: "Поздравлем!", message: "Ваш товар отправлен на модерацию и будет обработан в течение 48 часов. Как только товар пройдет модерацию, Вы получите уведомление о его статусе", preferredStyle: UIAlertControllerStyle.alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: {a in
                    self.navigationController?.popToRootViewController(animated: true)
                    self.dismiss(animated: true)
                }))
                self.present(alert, animated: true, completion: nil)
            }
            else {
                let alert = UIAlertController(title: "Не удалось опубликовать", message: error, preferredStyle: UIAlertControllerStyle.alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
                self.present(alert, animated: true, completion: nil)
            }
        }, completePublication: true)
    }
}