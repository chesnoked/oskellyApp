//
// Created by Виталий Хлудеев on 08.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductCommentController : UIViewController, UITextViewDelegate {

    private let textView = UITextView()

    var parentController : ProductViewController?
    var product : Product!

    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(textView)
        navigationItem.title = "Оставить комментарий"
        textView.snp.makeConstraints { m in
            m.edges.equalTo(view)
        }
        textView.tintColor = AppColors.lato()
        textView.font = MediumFont.systemFont(ofSize: 14)
        textView.delegate = self
        textView.text = "Комментарий..."
        textView.textColor = UIColor.lightGray
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okButtonPressed))
        navigationItem.rightBarButtonItem?.isEnabled = false
    }

    func textViewDidBeginEditing(_ textView: UITextView) {
        if textView.textColor == UIColor.lightGray {
            navigationItem.rightBarButtonItem?.isEnabled = true
            textView.text = nil
            textView.textColor = AppColors.textField()
        }
    }

    func textViewDidEndEditing(_ textView: UITextView) {
        if textView.text.isEmpty {
            navigationItem.rightBarButtonItem?.isEnabled = false
            textView.text = "Комментарий..."
            textView.textColor = UIColor.lightGray
        }
    }

    func okButtonPressed() {

        if(textView.text.isEmpty) {
            let alert = UIAlertController(title: "Введите комментарий", message: nil, preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
            self.present(alert, animated: true, completion: nil)
            return
        }


        navigationItem.rightBarButtonItem?.isEnabled = false
        
        GlobalProvider.instance.getApiRequester().publishProductComment(productId: product.id, text: textView.text, completeHandler: {c in})

        let account = GlobalProvider.instance.accountProvider.getCurrent(completionHandler: {account in

            let comment = Comment()
            comment.text = self.textView.text
            comment.user = account.nickname
            comment.userId = account.id
            comment.avatar = account.avatar
            comment.publishTime = GlobalProvider.instance.dateTimeConverter.fromDate(date: Date(), format: "dd.MM.yyyy HH:mm")

            self.parentController?.comments.insert(comment, at: 0)
            self.parentController?.tableView.reloadData()
            self.navigationController?.popViewController(animated: true)
        }, needSynchronize: false)
    }
}