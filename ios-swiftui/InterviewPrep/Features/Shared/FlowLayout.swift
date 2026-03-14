import SwiftUI

/**
 简易流式布局，用来展示标签。

 这里使用 `LazyVGrid(.adaptive)` 来做近似流式排列，稳定且容易维护。
 */
struct FlowLayout<Data: RandomAccessCollection, Content: View>: View where Data.Element: Hashable {
    let items: Data
    let spacing: CGFloat
    let content: (Data.Element) -> Content

    init(items: Data, spacing: CGFloat = 8, @ViewBuilder content: @escaping (Data.Element) -> Content) {
        self.items = items
        self.spacing = spacing
        self.content = content
    }

    var body: some View {
        LazyVGrid(
            columns: [GridItem(.adaptive(minimum: 72), spacing: spacing, alignment: .leading)],
            alignment: .leading,
            spacing: spacing
        ) {
            ForEach(Array(items), id: \.self) { item in
                content(item)
            }
        }
    }
}
