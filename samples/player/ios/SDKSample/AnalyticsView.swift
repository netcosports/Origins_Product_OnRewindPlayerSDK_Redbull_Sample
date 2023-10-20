//
//  AnalyticsView.swift
//  Demo
//
//  Created by Sergei Mikhan on 7/1/20.
//

import UIKit
import Astrolabe
import RxSwift
import RxCocoa

import OnRewindSDK

extension String {

  func height(withConstrainedWidth width: CGFloat, font: UIFont) -> CGFloat {
    let constraintRect = CGSize(width: width, height: .greatestFiniteMagnitude)
    let boundingBox = self.boundingRect(with: constraintRect, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: font], context: nil)

    return ceil(boundingBox.height)
  }
}

class AnalyticsCell: CollectionViewCell, Reusable {

  typealias Data = String
  private let title: UILabel = {
    let label = UILabel()
    label.textColor = .white
    label.font = AnalyticsCell.font
    label.numberOfLines = 0
    return label
  }()

  override func setup() {
    super.setup()
    contentView.backgroundColor = UIColor.black.withAlphaComponent(0.66)
    contentView.addSubview(title)
  }

  func setup(with data: Data) {
    title.text = data
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    title.frame = contentView.bounds.inset(by: UIEdgeInsets(top: 5.0, left: 5.0, bottom: 5.0, right: 5.0))
  }

  static let font = UIFont.systemFont(ofSize: 10.0)

  static func size(for data: Data, containerSize: CGSize) -> CGSize {
    let width = containerSize.width - 10.0
    let hegith = data.height(withConstrainedWidth: width, font: font) + 10.0
    return CGSize(width: containerSize.width, height: hegith + 5)
  }
}

class AnalyticsView: UIView {

  private let containerView = CollectionView<CollectionViewSource>()

  override init(frame: CGRect) {
    super.init(frame: frame)
    self.addSubview(containerView)

    self.isUserInteractionEnabled = false
    let layout = UICollectionViewFlowLayout()
    layout.minimumLineSpacing = 3.0
    layout.minimumInteritemSpacing = 3.0
    containerView.collectionViewLayout = layout
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    containerView.frame = bounds
  }

  private var events: [OnRewind.Analytics.Event] = []
  func handle(event: OnRewind.Analytics.Event) {
    events.insert(event, at: 0)
    let cells: [Cellable] = events.map {
      CollectionCell<AnalyticsCell>(data: $0.description)
    }
    containerView.source.sections = [Section(cells: cells)]
    containerView.reloadData()
  }
}
