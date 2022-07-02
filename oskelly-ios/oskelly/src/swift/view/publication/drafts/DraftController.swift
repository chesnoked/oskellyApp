//
//  DraftController.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 14.10.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit
import Dispatch
import DWImagePicker_Swift3

class DraftController: UIViewController, UITableViewDataSource, UITableViewDelegate, WDImagePickerDelegate {

    var drafts: [Draft] = []
    var itemsToDelete: [Draft] = []
    var backup : [Draft] = []

    let footer = UIView()
    let tableView = UITableView()
    let footerHeight: CGFloat = 130.0
    let sellButton = DarkButton()
    let background = UIImageView(image: UIImage(named: "assets/images/publication/Background.png")!)

    var editItem: UIBarButtonItem = UIBarButtonItem()
    var cancelItem: UIBarButtonItem = UIBarButtonItem()

    func imagePicker(imagePicker: WDImagePicker, pickedImage: UIImage) {

    }

    func imagePickerDidCancel(imagePicker: WDImagePicker) {

    }

    override func viewDidLoad() {
        super.viewDidLoad()

        editItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.editButtonPressed))
        cancelItem = UIBarButtonItem(title: "Отмена", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.cancelButtonPressed))

        navigationItem.title = "Продажа вещи"
        view.addSubview(tableView)
        tableView.dataSource = self
        tableView.delegate = self
        tableView.separatorStyle = .none
        tableView.register(DraftRow.self, forCellReuseIdentifier: "DraftRow")
        tableView.backgroundColor = AppColors.transparent()
        tableView.snp.makeConstraints { make -> Void in
            make.edges.equalTo(view).inset(UIEdgeInsetsMake(0, 0, footerHeight, 0))
        }
        view.addSubview(footer)
        footer.snp.makeConstraints { make -> Void in
            make.top.equalTo(tableView.snp.bottom)
            make.left.equalTo(view)
            make.right.equalTo(view)
            make.bottom.equalTo(view)
        }
        footer.addSubview(sellButton)
        sellButton.setTitle("На продажу", for: .normal)
        sellButton.snp.makeConstraints { make -> Void in
            make.center.equalTo(footer)
            make.width.equalTo(footer).multipliedBy(0.9)
        }
        sellButton.addTarget(self, action: #selector(DraftController.selButtonTapped(_:)), for: .touchUpInside)

        DispatchQueue.main.async {
            _ = DSCameraHandler(delegate_: self)
        }

        addBackground()

        hideTableView()
    }

    private func addBackground() {
        view.addSubview(background)
        background.contentMode = .scaleAspectFit
        background.addGradientLayer(frame: view.bounds, colors: [UIColor(red: 1, green: 1, blue: 1, alpha: 0), .white])
        background.layer.zPosition = -1
        background.snp.makeConstraints({ m in
            m.edges.equalTo(view).inset(UIEdgeInsetsMake(7,7,7,7))
        })

        let middleView = UIView()
        background.addSubview(middleView)
        middleView.backgroundColor = UIColor(red: 1, green: 1, blue: 1, alpha: 0.96)
        middleView.snp.makeConstraints({m in
            m.center.equalTo(background)
            m.width.equalTo(background)
            m.height.equalTo(190)
        })

        let circle = UIImageView(image: UIImage(named: "assets/images/publication/Circle.png")!)
        middleView.addSubview(circle)
        circle.contentMode = .scaleAspectFit
        circle.snp.makeConstraints({ m in
            m.centerX.equalTo(middleView)
            m.top.equalTo(middleView).inset(7)
        })

        let sellLabel = UILabel()
        sellLabel.font = RegularFont.systemFont(ofSize: 15)
        sellLabel.text = "Продажа"
        sellLabel.textAlignment = .center
        middleView.addSubview(sellLabel)
        sellLabel.snp.makeConstraints({ m in
            m.center.equalTo(middleView)
            m.top.equalTo(circle.snp.bottom)
        })

        let separator = UIView()
        middleView.addSubview(separator)
        separator.backgroundColor = .gray
        separator.snp.makeConstraints({m in
            m.centerX.equalTo(middleView)
            m.top.equalTo(sellLabel.snp.bottom).offset(15)
            m.height.equalTo(0.5)
            m.width.equalTo(150)
        })

        let descriptionLabel = UILabel()
        middleView.addSubview(descriptionLabel)
        descriptionLabel.text = "Продавайте дизайнерские вещи"
        descriptionLabel.textAlignment = .center
        descriptionLabel.font = RegularFont.systemFont(ofSize: 12)
        descriptionLabel.snp.makeConstraints({m in
            m.centerX.equalTo(middleView)
            m.top.equalTo(separator.snp.bottom).offset(15)
        })

        let descriptionLabel2 = UILabel()
        middleView.addSubview(descriptionLabel2)
        descriptionLabel2.text = "другим любителям моды"
        descriptionLabel2.textAlignment = .center
        descriptionLabel2.font = RegularFont.systemFont(ofSize: 12)
        descriptionLabel2.snp.makeConstraints({m in
            m.centerX.equalTo(middleView)
            m.top.equalTo(descriptionLabel.snp.bottom).offset(3)
        })
    }

    private func hideTableView() {
        footer.backgroundColor = UIColor(red: 1, green: 1, blue: 1, alpha: 0)
        tableView.isHidden = true
        background.isHidden = false
        navigationItem.rightBarButtonItem = nil
        navigationItem.leftBarButtonItem = nil
    }

    private func showTableView() {
        self.tableView.isHidden = false
        self.footer.backgroundColor = AppColors.transparent()
        self.background.isHidden = true
        navigationItem.rightBarButtonItem = editItem
//        navigationItem.leftBarButtonItem = cancelItem
    }

    override func viewWillAppear(_ animated: Bool) {
        GlobalProvider.instance.getApiRequester().getDrafts() { drafts in
            self.drafts = drafts
            self.tableView.reloadData()
            if(drafts.isEmpty) {
                self.hideTableView()
            }
            else {
                self.showTableView()
            }
        }
    }

    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return drafts.count
    }

    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DraftRow", for: indexPath as IndexPath) as! DraftRow
        cell.render(draft: drafts[indexPath.row])
        return cell
    }

    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {

       return 88.0
    }

    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)

        let draft = drafts[indexPath.row]
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "PublicationStepsController") as! PublicationStepsController
        GlobalProvider.instance.draftProvider.setCurrent(draft: draft)
        self.navigationController?.pushViewController(c, animated: true)
    }

    func selButtonTapped(_ sender: Any) {
        let c = storyboard!.instantiateViewController(withIdentifier: "SubmitCategoryController") as! SubmitCategoryController
        navigationController?.pushViewController(c, animated: true)
    }

    public func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }

    public func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            self.tableView.beginUpdates()
            itemsToDelete.append(drafts[indexPath.row])
            drafts.remove(at: indexPath.row)
            self.tableView.deleteRows(at: [indexPath], with: .left)
            self.tableView.endUpdates()
        }
    }

    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCellEditingStyle {
        if (tableView.isEditing) {
            return .delete
        }
        return .none
    }

    public func tableView(_ tableView: UITableView, titleForDeleteConfirmationButtonForRowAt indexPath: IndexPath) -> String? {
        return "Удалить"
    }

    func editButtonPressed() {
        tableView.setEditing(!tableView.isEditing, animated: true)
        if tableView.isEditing == true {
            backup = drafts // делам бэкап
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
            navigationItem.leftBarButtonItem = cancelItem
        }
        else {
            // отправка на сервер удаленных данных
            itemsToDelete.forEach({item in
                GlobalProvider.instance.getApiRequester().deleteDraft(draftId: item.id!)
            })
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
            navigationItem.leftBarButtonItem = nil
            if(drafts.isEmpty) {
                hideTableView() 
            }
        }
    }

    func cancelButtonPressed() {
        if tableView.isEditing == true {
            drafts = backup
            itemsToDelete.removeAll()
            tableView.reloadData()
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.005, execute: {
                self.tableView.setEditing(false, animated: true)
            })
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
            navigationItem.leftBarButtonItem = nil
        }
    }
}

extension UIImageView{
    func addGradientLayer(frame: CGRect, colors:[UIColor]){
        let gradient = CAGradientLayer()
        gradient.frame = frame
        gradient.colors = colors.map{$0.cgColor}
        self.layer.addSublayer(gradient)
    }
}