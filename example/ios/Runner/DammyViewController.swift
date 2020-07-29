//
//  DammyViewController.swift
//  Runner
//
//  Created by Adedamola Adeyemo on 28/07/2020.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import UIKit
import mobile_map_core

class DammyViewController: UIViewController {
    

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func onTapped() {
        print("HHHHH")
        let vc = TestViewController()
        self.present(vc, animated: true, completion: nil)
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
