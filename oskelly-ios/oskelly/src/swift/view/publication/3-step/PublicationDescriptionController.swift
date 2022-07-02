//
// Created by Виталий Хлудеев on 25.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationDescriptionController : UIViewController, UITextViewDelegate {

    let textView = UITextView()

    var draft: Draft!

    override func viewDidLoad() {
        super.viewDidLoad()
        self.draft = GlobalProvider.instance.draftProvider.getCurrent()
        view.addSubview(textView)
        navigationItem.title = "Описание"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "OK", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        textView.snp.makeConstraints { m in
            m.edges.equalTo(view)
        }
        textView.tintColor = AppColors.lato()
        textView.font = MediumFont.systemFont(ofSize: 14)
        textView.delegate = self
        if(draft.descriptionValue != nil && draft.descriptionValue! != "") {
            textView.text = draft.descriptionValue!
            textView.textColor = AppColors.textField()
        }
        else {
            textView.text = "Введите описание..."
            textView.textColor = UIColor.lightGray
        }
    }

    func textViewDidBeginEditing(_ textView: UITextView) {
        if textView.textColor == UIColor.lightGray {
            textView.text = nil
            textView.textColor = AppColors.textField()
        }
    }

    func textViewDidEndEditing(_ textView: UITextView) {

        draft.descriptionValue = textView.text
        GlobalProvider.instance.draftProvider.setCurrent(draft: draft)
        GlobalProvider.instance.draftProvider.publish(draft: draft) {d in}

        if textView.text.isEmpty {
            textView.text = "Введите описание..."
            textView.textColor = UIColor.lightGray
        }
    }

    func okPressed() {
        navigationController?.popViewController(animated: true)
    }
}