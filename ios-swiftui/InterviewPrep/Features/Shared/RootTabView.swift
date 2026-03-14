import SwiftUI

struct RootTabView: View {
    var body: some View {
        TabView {
            NavigationStack {
                HomeView()
            }
            .tabItem {
                Label("出题", systemImage: "sparkles.rectangle.stack")
            }

            NavigationStack {
                HistoryView()
            }
            .tabItem {
                Label("题集", systemImage: "clock.arrow.circlepath")
            }
        }
        .tint(.brown)
    }
}
