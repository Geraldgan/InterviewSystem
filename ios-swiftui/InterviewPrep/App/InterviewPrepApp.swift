import SwiftUI

/**
 App 入口。

 这里把全局状态 `InterviewStore` 注入到视图树里，后续所有页面都可以共用同一套接口访问能力。
 */
@main
struct InterviewPrepApp: App {
    @StateObject private var store = InterviewStore()

    var body: some Scene {
        WindowGroup {
            RootTabView()
                .environmentObject(store)
        }
    }
}
