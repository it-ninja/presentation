//
//  WGNMyGameVC.h
//  WGN
//
//  Created by Divya Prakash on 19/08/14.
//  Copyright (c) 2014 Kleward Consulting Pvt Ltd. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WGNMyGameVC : UIViewController<UITableViewDataSource,UITableViewDelegate>
@property(nonatomic,strong)IBOutlet UITableView *myGameTableView;

@end
