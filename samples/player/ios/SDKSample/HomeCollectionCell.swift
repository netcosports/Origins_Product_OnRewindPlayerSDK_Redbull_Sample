import Astrolabe
import PinLayout
import RxSwift

final class HomeCollectionCell: CollectionViewCell, Reusable {

  let label: UILabel = {
    let label = UILabel()
    label.textColor = .black
    label.font = .systemFont(ofSize: 14.0)
    label.numberOfLines = 2
    return label
  }()

  let separator: UIView = {
    let view = UIView()
    view.backgroundColor = UIColor.lightGray
    return view
  }()

  let fullscreen: UIButton = {
    let button = UIButton()
    button.setTitle("Fullscreen", for: .normal)
    button.setTitleColor(UIColor.blue, for: .normal)
    return button
  }()

  let list: UIButton = {
    let button = UIButton()
    button.setTitle("List", for: .normal)
    button.setTitleColor(UIColor.blue, for: .normal)
    return button
  }()

  let embedded: UIButton = {
    let button = UIButton()
    button.setTitle("Embedded", for: .normal)
    button.setTitleColor(UIColor.blue, for: .normal)
    return button
  }()

  override func setup() {
    super.setup()
    contentView.addSubviews(label,
                            fullscreen,
                            list,
                            embedded,
                            separator)
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    label.pin.top(12).horizontally(12).sizeToFit(.width)
    separator.pin.bottom().horizontally().height(1.0)

    let width = self.width / 3.0
    fullscreen.pin.start().width(width).below(of: label).bottom()
    list.pin.after(of: fullscreen).width(width).below(of: label).bottom()
    embedded.pin.after(of: list).width(width).below(of: label).bottom()
  }

  typealias Data = ViewModel

  enum Events {
    case fullscreen
    case embeded
    case list
  }

  struct ViewModel {
    let title: String
    let events: AnyObserver<Events>
  }

  private var bindDisposeBag = DisposeBag()
  func setup(with data: Data) {
    label.text = data.title
    bindDisposeBag = DisposeBag()
    fullscreen.rx.tap.map { Events.fullscreen }.bind(to: data.events).disposed(by: bindDisposeBag)
    list.rx.tap.map { Events.list }.bind(to: data.events).disposed(by: bindDisposeBag)
    embedded.rx.tap.map { Events.embeded }.bind(to: data.events).disposed(by: bindDisposeBag)
  }

  class func size(for data: Data, containerSize: CGSize) -> CGSize {
    return CGSize(width: containerSize.width, height: 120.0)
  }
}
